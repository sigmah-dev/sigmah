package org.sigmah.shared.dto.pivot.content;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class QuarterCategory implements DimensionCategory {

    private int year;
    private int quarter;

    public QuarterCategory() {
    }

    public QuarterCategory(int year, int quarter) {
        this.year = year;
        this.quarter = quarter;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

	@Override
    public Comparable getSortKey() {
        return year * 10 + quarter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuarterCategory that = (QuarterCategory) o;

        if (quarter != that.quarter) {
            return false;
        }
        if (year != that.year) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + quarter;
        return result;
    }

    @Override
    public String toString() {
        return "QuarterCategory{" + year + "Q" + quarter + "}";
    }

	@Override
	public DimensionCategory getParent() {
		return null;
	}

	@Override
	public boolean hasParent() {
		return false;
	}
}
