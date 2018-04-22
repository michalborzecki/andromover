package sr.andromover.click;

public enum ButtonType {
    Left("left"),
    Right("right"),
    None("none");

    private String name;

    ButtonType(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
