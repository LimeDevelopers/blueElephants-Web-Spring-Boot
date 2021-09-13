package kr.or.btf.web.domain.web.enums;

public enum AppRollType {
    CREW("지지크루"),
    SUPPORT("지지선언"),
    PARTNERS("파트너스"),
    CONTEST("공모전"),
    EVENT("행사"),
    DECLARE("지지선언");

    final private String name;

    public String getName() {
        return name;
    }

    private AppRollType(String name){
        this.name = name;
    }
}
