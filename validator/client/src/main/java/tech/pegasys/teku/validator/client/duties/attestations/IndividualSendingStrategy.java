/*
 * Copyright 2021 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.validator.client.duties.attestations;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import tech.pegasys.teku.infrastructure.async.SafeFuture;
import tech.pegasys.teku.spec.datastructures.operations.Attestation;
import tech.pegasys.teku.spec.datastructures.operations.SignedAggregateAndProof;
import tech.pegasys.teku.validator.api.ValidatorApiChannel;
import tech.pegasys.teku.validator.client.duties.DutyResult;
import tech.pegasys.teku.validator.client.duties.ProductionResult;
import tech.pegasys.teku.validator.client.duties.RestApiReportedException;

public class IndividualSendingStrategy<T> implements SendingStrategy<T> {
  private final Function<ProductionResult<T>, SafeFuture<DutyResult>> sendFunction;

  IndividualSendingStrategy(
      final Function<ProductionResult<T>, SafeFuture<DutyResult>> sendFunction) {
    this.sendFunction = sendFunction;
  }

  public static IndividualSendingStrategy<Attestation> createAttestationSender(
      final ValidatorApiChannel validatorApiChannel) {
    return new IndividualSendingStrategy<>(
        result ->
            validatorApiChannel
                .sendSignedAttestations(List.of(result.getMessage().orElseThrow()))
                .thenApply(
                    errors -> {
                      if (errors.isEmpty()) {
                        return result.getResult();
                      } else {
                        return DutyResult.forError(
                            result.getValidatorPublicKeys(),
                            new RestApiReportedException(errors.get(0).getMessage()));
                      }
                    }));
  }

  public static IndividualSendingStrategy<SignedAggregateAndProof> createAggregateSender(
      final ValidatorApiChannel validatorApiChannel) {
    return new IndividualSendingStrategy<>(
        result -> {
          validatorApiChannel.sendAggregateAndProof(result.getMessage().orElseThrow());
          return SafeFuture.completedFuture(result.getResult());
        });
  }

  @Override
  public SafeFuture<DutyResult> send(final Stream<SafeFuture<ProductionResult<T>>> attestations) {
    return DutyResult.combine(
        attestations.map(future -> future.thenCompose(this::sendIfNotFailed)).collect(toList()));
  }

  private SafeFuture<DutyResult> sendIfNotFailed(final ProductionResult<T> result) {
    if (result.failedToProduceMessage()) {
      return SafeFuture.completedFuture(result.getResult());
    } else {
      return sendAttestation(result);
    }
  }

  private SafeFuture<DutyResult> sendAttestation(final ProductionResult<T> result) {
    return sendFunction
        .apply(result)
        .exceptionally(error -> DutyResult.forError(result.getValidatorPublicKeys(), error));
  }
}