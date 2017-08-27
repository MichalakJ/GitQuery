package model;

import constants.ObjectType;

public class GitObject {
    private ObjectType type;
    private String sha1;
    private int offSet;

    Integer diffOffset;
    String diffSha1;

    public GitObject(ObjectType type, String sha1, int offSet) {
        this.type = type;
        this.sha1 = sha1;
        this.offSet = offSet;
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public Integer getDiffOffset() {
        return diffOffset;
    }

    public void setDiffOffset(Integer diffOffset) {
        this.diffOffset = diffOffset;
    }

    public String getDiffSha1() {
        return diffSha1;
    }

    public void setDiffSha1(String diffSha1) {
        this.diffSha1 = diffSha1;
    }

    @Override
    public String toString() {
        return "GitObject{" +
                "type=" + type +
                ", sha1='" + sha1 + '\'' +
                ", offSet=" + offSet +
                "}\n";
    }
}
