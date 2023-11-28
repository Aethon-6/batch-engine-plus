package com.engine.enums;

public enum NodeTypeEnum {
    ADD("add", "与合并节点"),
    CHOOSE("choose", "选择节点"),
    COMMUNICATE("communicate", "沟通节点"),
    OR("or", "或合并节点"),
    UNIT("unit", "单元节点"),
    SPLIT("split", "单元节点");

    private String type;
    private String name;

    NodeTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static NodeTypeEnum getType(String type) {
        for (NodeTypeEnum nodeTypeEnum : NodeTypeEnum.values()) {
            if (nodeTypeEnum.type.equalsIgnoreCase(type)) {
                return nodeTypeEnum;
            }
        }
        return null;
    }

    public static String getName(String type) {
        for (NodeTypeEnum nodeTypeEnum : NodeTypeEnum.values()) {
            if (nodeTypeEnum.type.equalsIgnoreCase(type)) {
                return nodeTypeEnum.name;
            }
        }
        return "";
    }
}

class t {
    public static void main(String[] args) {
        System.out.println(NodeTypeEnum.getName("add"));
    }
}