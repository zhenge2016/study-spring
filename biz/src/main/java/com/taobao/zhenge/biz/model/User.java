package com.taobao.zhenge.biz.model;

import lombok.Data;

@Data
public class User {

    private String username;

    private String password;

    private String email;

    private int age;

    private Address address;

}
