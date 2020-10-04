package org.bahmni.feed.openerp.domain.encounter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSObservationGroupMemberConcept {
    private String uuid;
    private String name;
    private String shortName;
    private String conceptClass;

    public String getuuid() {
        return uuid;
    }

    public void setuuid(String uuid) {
        this.uuid = uuid;
    }
   
    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }
    public String getshortName() {
        return shortName;
    }

    public void setshortName(String shortName) {
        this.shortName = shortName;
    }
    public String getconceptClass() {
        return conceptClass;
    }

    public void setconceptClass(String conceptClass) {
        this.conceptClass = conceptClass;
    }
    
}