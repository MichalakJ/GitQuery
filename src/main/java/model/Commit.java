package model;

import constants.ObjectType;

import java.util.Date;

public class Commit extends GitObject{
    private Date date;
    private String author;
    private String email;
    private String comment;
    private String parent;
    private String tree;

    public Commit(ObjectType type, String sha1, int offSet) {
        super(type, sha1, offSet);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTree() {
        return tree;
    }

    public void setTree(String tree) {
        this.tree = tree;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "date=" + date +
                ", author='" + author + '\'' +
                ", email='" + email + '\'' +
                ", comment='" + comment + '\'' +
                ", parent='" + parent + '\'' +
                ", tree='" + tree + '\'' +
                ", diffOffset=" + diffOffset +
                ", diffSha1='" + diffSha1 + '\'' +
                '}';
    }
}
