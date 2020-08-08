package com.orin.booruviewer.entity;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Objects;

public class Tag implements Serializable {
    public enum Type {
        COPYRIGHT("copyright"),
        CHARACTER("character"),
        ARTIST("artist"),
        GENERAL("tag"),
        METADATA("metadata"),
        DEPRECATED("deprecated"),
        INVALID("invalid");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public static Type fromString(String name) {
            for (Type t : Type.values()) {
                if (t.toString().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return Type.INVALID;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum TagColor {
        PURPLE("#AA00AA"),
        GREEN("#2aaa00"),
        RED("#AA0000"),
        BLUE("#337ab7"),
        ORANGE("#FF8800"),
        GRAY("#C0C0C0");

        private final String value;

        TagColor(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private String type;
    private String name;
    private int color;

    public Tag() {

    }

    public Tag(String name) {
        this.setName(name);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        setColor(Type.fromString(type));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    private void setColor(int color) {
        this.color = color;
    }

    private void setColor(Type type) {
        switch (type) {
            case GENERAL:
                setColor(Color.parseColor(TagColor.BLUE.toString()));
                break;
            case CHARACTER:
                setColor(Color.parseColor(TagColor.GREEN.toString()));
                break;
            case COPYRIGHT:
                setColor(Color.parseColor(TagColor.PURPLE.toString()));
                break;
            case METADATA:
                setColor(Color.parseColor(TagColor.ORANGE.toString()));
                break;
            case ARTIST:
                setColor(Color.parseColor(TagColor.RED.toString()));
                break;
            case DEPRECATED:
                setColor(Color.parseColor(TagColor.GRAY.toString()));
                break;
            case INVALID:
                setColor(Color.TRANSPARENT);
                break;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
