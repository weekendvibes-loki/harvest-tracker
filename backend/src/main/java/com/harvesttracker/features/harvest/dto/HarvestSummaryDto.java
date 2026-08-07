package com.harvesttracker.features.harvest.dto;

import java.math.BigDecimal;

public class HarvestSummaryDto {

    private HarvestSummaryDto() {
    }

    public static class HarvestSummaryResponse {
        private long totalHarvests;
        private BigDecimal totalQuantity;
        private long draftCount;
        private long confirmedCount;
        private long storedCount;
        private long soldCount;
        private long gradeACount;
        private long gradeBCount;
        private long gradeCCount;
        private long gradeRejectCount;

        public long getTotalHarvests() {
            return totalHarvests;
        }

        public void setTotalHarvests(long totalHarvests) {
            this.totalHarvests = totalHarvests;
        }

        public BigDecimal getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(BigDecimal totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public long getDraftCount() {
            return draftCount;
        }

        public void setDraftCount(long draftCount) {
            this.draftCount = draftCount;
        }

        public long getConfirmedCount() {
            return confirmedCount;
        }

        public void setConfirmedCount(long confirmedCount) {
            this.confirmedCount = confirmedCount;
        }

        public long getStoredCount() {
            return storedCount;
        }

        public void setStoredCount(long storedCount) {
            this.storedCount = storedCount;
        }

        public long getSoldCount() {
            return soldCount;
        }

        public void setSoldCount(long soldCount) {
            this.soldCount = soldCount;
        }

        public long getGradeACount() {
            return gradeACount;
        }

        public void setGradeACount(long gradeACount) {
            this.gradeACount = gradeACount;
        }

        public long getGradeBCount() {
            return gradeBCount;
        }

        public void setGradeBCount(long gradeBCount) {
            this.gradeBCount = gradeBCount;
        }

        public long getGradeCCount() {
            return gradeCCount;
        }

        public void setGradeCCount(long gradeCCount) {
            this.gradeCCount = gradeCCount;
        }

        public long getGradeRejectCount() {
            return gradeRejectCount;
        }

        public void setGradeRejectCount(long gradeRejectCount) {
            this.gradeRejectCount = gradeRejectCount;
        }
    }
}
