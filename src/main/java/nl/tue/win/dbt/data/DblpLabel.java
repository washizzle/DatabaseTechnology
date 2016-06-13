package nl.tue.win.dbt.data;

public enum DblpLabel {
    BEGINNER, // 0-2
    JUNIOR, // 3-5
    SENIOR, // 6-10
    PROFESSOR, //>=11
    ;

    public static DblpLabel calculateLabel(int frequency) {
        DblpLabel rv;
        if(frequency < 0) {
            throw new IllegalArgumentException("Expect a natural number");
        } else if(frequency <= 2) {
            rv = BEGINNER;
        } else if(frequency <= 5) {
            rv = JUNIOR;
        } else if(frequency <= 10) {
            rv = SENIOR;
        } else {
            rv = PROFESSOR;
        }
        return rv;
    }
}
