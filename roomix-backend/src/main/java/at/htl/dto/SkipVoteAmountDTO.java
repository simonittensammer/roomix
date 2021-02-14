package at.htl.dto;

public class SkipVoteAmountDTO {

    private int currentAmount;
    private int amountNeeded;

    public SkipVoteAmountDTO() {
    }

    public SkipVoteAmountDTO(int currentAmount, int amountNeeded) {
        this.currentAmount = currentAmount;
        this.amountNeeded = amountNeeded;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getAmountNeeded() {
        return amountNeeded;
    }

    public void setAmountNeeded(int amountNeeded) {
        this.amountNeeded = amountNeeded;
    }
}
