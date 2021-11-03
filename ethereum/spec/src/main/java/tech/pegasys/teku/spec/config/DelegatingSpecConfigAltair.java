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

package tech.pegasys.teku.spec.config;

import java.util.Objects;
import java.util.Optional;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.ssz.type.Bytes4;

public class DelegatingSpecConfigAltair extends DelegatingSpecConfig implements SpecConfigAltair {

  private final SpecConfigAltair specConfigAltair;

  public DelegatingSpecConfigAltair(final SpecConfigAltair specConfig) {
    super(specConfig);
    this.specConfigAltair = specConfig;
  }

  @Override
  public Bytes4 getAltairForkVersion() {
    return specConfigAltair.getAltairForkVersion();
  }

  @Override
  public UInt64 getAltairForkEpoch() {
    return specConfigAltair.getAltairForkEpoch();
  }

  @Override
  public UInt64 getInactivityPenaltyQuotientAltair() {
    return specConfigAltair.getInactivityPenaltyQuotientAltair();
  }

  @Override
  public int getMinSlashingPenaltyQuotientAltair() {
    return specConfigAltair.getMinSlashingPenaltyQuotientAltair();
  }

  @Override
  public int getProportionalSlashingMultiplierAltair() {
    return specConfigAltair.getProportionalSlashingMultiplierAltair();
  }

  @Override
  public int getSyncCommitteeSize() {
    return specConfigAltair.getSyncCommitteeSize();
  }

  @Override
  public UInt64 getInactivityScoreBias() {
    return specConfigAltair.getInactivityScoreBias();
  }

  @Override
  public UInt64 getInactivityScoreRecoveryRate() {
    return specConfigAltair.getInactivityScoreRecoveryRate();
  }

  @Override
  public int getEpochsPerSyncCommitteePeriod() {
    return specConfigAltair.getEpochsPerSyncCommitteePeriod();
  }

  @Override
  public int getMinSyncCommitteeParticipants() {
    return specConfigAltair.getMinSyncCommitteeParticipants();
  }

  @Override
  public Optional<SpecConfigAltair> toVersionAltair() {
    return Optional.of(this);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final DelegatingSpecConfigAltair that = (DelegatingSpecConfigAltair) o;
    return Objects.equals(specConfigAltair, that.specConfigAltair);
  }

  @Override
  public int hashCode() {
    return Objects.hash(specConfigAltair);
  }
}