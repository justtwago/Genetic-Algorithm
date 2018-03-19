class Sector {
    private double start;
    private double end;

    Sector(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public boolean contains(double number) {
        return number < end && number >= start;
    }
}
