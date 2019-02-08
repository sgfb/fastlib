package com.fastlib;

import com.fastlib.annotation.Database;

import java.util.List;

/**
 * Created by sgfb on 18/10/15.
 * E-mail: 602687446@qq.com
 */
public class MyTemplate{
    @Database(keyPrimary = true)
    public int categoryId;
    public String cover;
    public String name;
    public String resume;
    public List<Integer> mainBody;
    public List<TemplateBean> templateLists;

}
