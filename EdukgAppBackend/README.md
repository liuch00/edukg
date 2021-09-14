# EdukgAppBackend
Java Course Project, backend part

## 构建说明
使用SpringBoot框架  
通过SpringInitializer完成初始化，初始化依赖为Web, Security, JPA, Mysql，并且手动添加对JWT的依赖  

## 知识图谱访问说明
对于一个常规的知识图谱而言，最靠谱的访问方式就是通过uri直接访问，在edukg中通过label/name/实体名称直接访问也是类似的效果  
而实体搜索功能不保证得到合适的结果  
例如，edukg网站提供的图谱展示，每个学科首页的那一圈实体，如果直接用实体名称访问则都能够找到对应的数据，但是放进实体搜索接口则只能拿到奇怪的结果，甚至无法找到对应数据  
所以学科分类列表接口只能返回字符串数组，不能通过搜索功能构造一组数据  

## 接口说明
### 权限相关
(本部分接口均不需要登录即可访问)  
##### post: /auth/account
- 功能：注册
- 参数 (Request Body，JSON格式)
    - username: 用户名
    - password: 密码
- 返回值
    - 成功
        - 200   
        - token
    - 失败
        - 400
        - "Username already occupied"

##### get: /auth/token
- 功能：登录
- 参数
    - username: 用户名
    - password: 密码
- 返回值
    - 成功
        - 200
        - token
    - 失败
        - 400
        - "Username and password not matching"

##### 其他需要权限的接口如何访问
上述两个接口会返回一个token，将此token放入请求的header中，对应的key设置为"Authorization"  
如果遇到401返回码，则token存在错误，或者已过期，需要重新请求token  
(token有效期可后续再讨论，也可根据调试需求进行调整)  
用户退出登录时，简单地将本地存储的token清除即可，不需要访问服务端  

### 服务接口
##### get: /service/entity-list-by-search
- 功能：实体搜索
- 参数
    - course: 学科代号
    - key: 搜索关键词
- 返回值
    - 成功
        - 200
        - a list (元素详情见下)
    - 失败 课程代号错误
        - 400
        - "Invalid course code"

```
{
  "label": "", // String 实体名称
  "category": "", // String 实体类别
  "uri": "" // String 实体的uri
}
```

##### get: /service/entity-list-by-course
- 功能：学科分类列表
- 参数
    - course: 学科代号
- 返回值
    - 成功
        - 200
        - String数组
  - 失败 课程代号错误
      - 400
      - "Invalid course code"

##### post: /service/answer-for-question
- 功能：问答
- 参数
    - course: 课程代号
    - text: 问题文本
- 返回值
    - 成功
        - 200
        - a list (元素格式见下)
  - 失败 课程代号错误
      - 400
      - "Invalid course code"

列表中每个元素的内容：
```
{
    "all": "",
    "fsanswer": "",
    "subject": "李白", // 答案相关实体
    "message": "",
    "tamplateContent": "(?<title>(.*)?)字(是)?什么(.*)?", // 自然语言解析
    "fs": 0,
    "filterStr": "",
    "subjectUri": "http://edukb.org/knowledge/0.1/instance/chinese#-2238b41d594489a133577ba3d2c1f54f", // 答案相关实体uri
    "predicate": "字", // 答案涉及的实体属性
    "score": 28.284271247461902, // 相关度得分
    "answerflag": false,
    "attention": "",
    "fsscore": "",
    "value": "太白" // 答案值
}
```
        

##### get: /service/entity-detail
- 功能：实体详情
- 参数
    - course: 课程代号
    - name: 实体名称/标签
- 返回值
    - 成功
        - 200
        - a map (元素详情见下)
  - 失败 课程代号错误
      - 400
      - "Invalid course code"

```
{
  "label": "", // 实体名称
  "uri": "", // 实体的uri
  "property": list [ // 实体属性
    "predicate": "", // 属性uri
    "predicateLabel": "", // 属性名称
    "object": "" // 属性值
  ],
  "content": list [ // 实体上下文
    "predicate": "", // 关系属性uri
    "predicate_label": "", // 关系属性名称
    "object_label": "", // 实体名称
    "object": "" // 实体uri
    "subject_label": "", // 实体名称
    "subject": "" // 实体uri
  ]
}
```
content中每个元素的object前缀属性和subject前缀属性，原则上只有其中一组被赋值，另一组被置为null，可根据哪一组有值来判断关系箭头的指向  

##### post: /service/instance-linkage
- 功能：实体链接
- 参数
    - course: 课程代号
    - text: 待解析的文本
- 返回值
    - 成功
        - 200
        - a map (元素详情见下)
  - 失败 课程代号错误
      - 400
      - "Invalid course code"

```
{
  "results": [ // 一个列表，其中每个元素如下
    {
      "entity_type": "", // 类型
      "entity_url": "", // uri
      "start_index": 0, // Long 名称在文本中的起点
      "end_index": 1, // Long 名称在文本中的终点
      "entity": "" // 实体名称
    },
  ]
}
```

##### get: /service/exercise
- 功能：习题列表
- 参数
    - uriName: 实体名称
- 返回值
    - 成功
        - 200
        - a map (元素详情见下)

```
{
  "results": [ // 一个列表，其中每个元素如下
    {
      "qAnswer": "", // 答案
      "id": 1234, // 问题编号
      "qBody": "" // 题干
    },
  ]
}
```
##### get: /service/related-entities
- 功能：关联实体 (可用于制作知识大纲功能)
- 参数
    - course: 学科代号
    - subjectName: 实体名称
- 返回值
    - 成功
        - 200
        - a list (元素详情见下)
  - 失败 课程代号错误
      - 400
      - "Invalid course code"

列表中每个元素的格式如下：  
```
{
    "all": "",
    "fsanswer": "",
    "subject": "", // 属性值，html模板
    "message": "",
    "tamplateContent": "",
    "fs": 0,
    "filterStr": "",
    "subjectUri": "",
    "predicate": "", // 关系属性名称
    "score": 0,
    "answerflag": false,
    "attention": "",
    "fsscore": "",
    "value": "" // 搜索知识点
}
```
##### post: /service/favorite
- 功能：添加收藏
- 参数
    - course: 学科代号
    - label: 实体名称
- 返回值
    - 成功
        - 200
        - course: 学科代号
        - label: 实体名称
    - 失败
        - 400
        - "Favorite already exists"

##### delete: /service/favorite
- 功能：删除收藏
- 参数 (delete请求的传参方式类似get，通过url后面拼接进行)
    - course: 学科代号
    - label: 实体名称
- 返回值
    - 成功
        - 200
        - course: 学科代号
        - label: 实体名称
    - 失败
        - 400
        - "Favorite not found"

##### get: /service/favorite
- 功能：获取收藏列表
- 无参数
- 返回值
    - 成功
        - 200
        - a list (元素详情见下)
```
{
  "course": "",
  "label": ""
}
```

##### post: /service/history
- 功能：添加历史记录
- 参数
    - course: 学科代号
    - label: 实体名称
- 返回值
    - 成功
        - 200
- 注
    - 仅保留最新的50条记录

##### get: /service/history
- 功能：获取历史记录
- 无参数
- 返回值
    - 成功
        - 200
        - a list

```
[
  {
    "course": "",
    "label": "",
    "time": 1630758567002 // 记录添加时间
  }
]
```

### 图数据库接口
##### post: /neo/framework
- 功能：获得知识大纲，内容是中心实体周围两跳范围内的实体，以层次化的方式给出
- 参数
    - uri: 中心实体的uri
- 返回值
    - 成功
        - 200
        - 元素详情见下
    - 失败
        - 200
        - 词典中每个元素的值均为 null

返回值：  
```
{
  'uri': '',
  'label': '',
  'rel': '', # 与上级实体关联的关系，优先以标签形式给出
  'sub': []
}
sub 中的每一项都递归采用相同的结构
若某个实体不再有后续的子实体关联，则 sub 为 null  
rel 优先以标签形式给出，若数据库中没有相关标签，则以 uri 形式给出
```

##### post: /neo/path
- 功能：给定两个实体，找出至多5条两者间的最短路
- 参数
    - s: 实体之一
    - t: 实体之一
    - 注：路径并没有要求关系的指向，所有关系被视为双向的，所以s和t的顺序理论上没有关系
- 返回值
    - 成功
        - 200
        - 元素详情见下
    - 失败
        - 200
        - 空列表

```
[
    [
        {
            'type': '',  # Entity or Relation
            'uri': '',
            'label': ''
        },
        ...
    ],  # 这是一条路径
    ...
]
```

##### post: neo/recommendation
- 功能：给出推荐实体，用于学科分类列表，保证推荐的实体都有 label
- 参数
    - history: 用户浏览历史，要求是一个String数组，里面每一项都是uri
    - course: 学科代号，确保history中每一项都属于声称的学科
- 返回值
    - 成功
        - 200
        - 一个列表，里面每一项如下

```
{
    'uri': '',
    'label': ''
},
...
```
