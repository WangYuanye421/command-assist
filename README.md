
<!-- Plugin description -->
**IntelliJ Platform Plugin Template** is a repository that provides a pure template to make it easier to create a new plugin project (check the [Creating a repository from a template][gh:template] article).

The main goal of this template is to speed up the setup phase of plugin development for both new and experienced developers by preconfiguring the project scaffold and CI, linking to the proper documentation pages, and keeping everything organized.

[gh:template]: https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template
<!-- Plugin description end -->

If you're still not quite sure what this is all about, read our introduction: [What is the IntelliJ Platform?][docs:intro]



# 草稿
- 常用命令列表 (可运行)
- cheatsheet
- 语言作为空间隔离
  - 
  
.command_assist

  commonly_used_list
    
  cheatsheets todo 备忘录,格式不做处理,保留用户格式?
    java
    python
    shell

```json
// config.json 

// commonly_used_list.json
{
  "schemas": [
    {
      "name": "zsh",
      "cmds": [
        {
          "cmd": "ls",
          "remark": "显示文件"
        },
        {
          "cmd": "ls -a",
          "remark": "显示文件 所有"
        }
      ]
    },
    {
      "name": "java",
      "cmds": [
        {
          "cmd": "javac",
          "remark": "javac"
        },
        {
          "cmd": "java -p",
          "remark": "java -p"
        }
      ]
    }
  ]
}


```
