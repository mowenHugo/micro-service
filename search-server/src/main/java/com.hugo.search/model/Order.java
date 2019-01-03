package com.hugo.search.model;

/**
 * 排序规则
 */
public class Order {
	private String nestedPath;//嵌套路径
	private String name; //排序字段名称
	private String type;//排序字段类型

	public Order(String nestedPath, String name, String type) {
        this.nestedPath = nestedPath;
		this.name = name;
		this.type = type;
	}

    public String getNestedPath() {
        return nestedPath;
    }

    public void setNestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
