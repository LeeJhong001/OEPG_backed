# OEPG - 在线考试平台生成器 (Online Exam Paper Generator)

## 📖 项目简介

OEPG 是一个基于 Spring Boot 的在线考试平台后端服务，提供完整的题库管理、考试组织和用户管理功能。系统支持多种题型管理、智能组卷、考试监控等核心功能，适用于教育机构、企业培训等场景。

## 🏗️ 技术架构

### 核心技术栈
- **后端框架**: Spring Boot 2.6.13
- **安全框架**: Spring Security + JWT
- **数据库**: MySQL 8.0+
- **ORM 框架**: MyBatis Plus 3.5.3
- **开发语言**: Java 8
- **构建工具**: Maven
- **其他组件**: Lombok, Validation

### 架构特点
- 🔐 基于 JWT 的无状态认证
- 🛡️ Spring Security 安全框架
- 📊 MyBatis Plus 数据库操作
- 🔄 RESTful API 设计
- 📝 完整的请求验证机制

## 🚀 快速开始

### 环境要求
- Java 8+
- Maven 3.6+
- MySQL 8.0+
- IDE (推荐 IntelliJ IDEA)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd OEPG_backed
   ```

2. **数据库配置**
   ```sql
   # 创建数据库
   CREATE DATABASE oepg CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **配置文件修改**
   
   修改 `src/main/resources/application.yaml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/oepg?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
       username: your_username
       password: your_password
   ```

4. **安装依赖**
   ```bash
   mvn clean install
   ```

5. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

   或者直接运行主类：`org.example.oepg.OEPGApplication`

6. **验证启动**
   
   服务默认运行在 `http://localhost:8081`

## 🎯 核心功能

### 用户管理
- ✅ 用户注册/登录
- ✅ JWT 身份认证
- ✅ 角色权限管理
- ✅ 用户信息管理

### 题库管理
- ✅ 题目创建/编辑/删除
- ✅ 题目分类管理
- ✅ 多种题型支持
- ✅ 题目批量导入

### 考试管理
- ✅ 考试创建与配置
- ✅ 试卷生成
- ✅ 考试记录管理
- ✅ 成绩统计分析

## 📋 API 接口

### 认证相关
```
POST /api/auth/login          # 用户登录
POST /api/auth/register       # 用户注册
GET  /api/auth/info          # 获取用户信息
POST /api/auth/logout        # 用户登出
```

### 题目管理
```
GET    /api/questions         # 获取题目列表
POST   /api/questions         # 创建题目
GET    /api/questions/{id}    # 获取题目详情
PUT    /api/questions/{id}    # 更新题目
DELETE /api/questions/{id}    # 删除题目
```

### 题目分类
```
GET    /api/categories        # 获取分类列表
POST   /api/categories        # 创建分类
PUT    /api/categories/{id}   # 更新分类
DELETE /api/categories/{id}   # 删除分类
```

### 用户管理
```
GET    /api/users            # 获取用户列表
POST   /api/users            # 创建用户
GET    /api/users/{id}       # 获取用户详情
PUT    /api/users/{id}       # 更新用户
DELETE /api/users/{id}       # 删除用户
```

## 🗄️ 数据库设计

### 主要数据表
- `user` - 用户信息表
- `question` - 题目信息表
- `question_category` - 题目分类表
- `exam` - 考试信息表
- `exam_paper` - 试卷表
- `exam_record` - 考试记录表
- `paper_question` - 试卷题目关联表
- `subject` - 科目表

## 🔧 开发指南

### 项目结构
```
src/main/java/org/example/oepg/
├── config/              # 配置类
├── constants/           # 常量定义
├── controller/          # 控制器层
├── dto/                # 数据传输对象
│   ├── req/            # 请求 DTO
│   └── res/            # 响应 DTO
├── entity/             # 实体类
├── enums/              # 枚举类
├── exception/          # 异常处理
├── repository/         # 数据访问层
├── service/            # 服务层
│   └── impl/          # 服务实现
└── util/              # 工具类
```

### 编码规范
- 使用 Lombok 减少样板代码
- 遵循 RESTful API 设计原则
- 统一的异常处理机制
- 完整的参数校验

## 🔒 安全机制

- **JWT 认证**: 无状态的 Token 认证机制
- **密码加密**: BCrypt 加密存储用户密码
- **权限控制**: 基于角色的访问控制 (RBAC)
- **跨域配置**: 支持前端跨域访问
- **参数校验**: 完整的输入参数验证

## 🧪 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn integration-test

# 生成测试报告
mvn surefire-report:report
```

## 📦 部署

### 打包应用
```bash
mvn clean package
```

### Docker 部署 (可选)
```dockerfile
FROM openjdk:8-jre-slim
COPY target/OEPG_backed-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📝 开发日志

### v0.0.1-SNAPSHOT
- ✅ 基础框架搭建
- ✅ 用户认证模块
- ✅ 题目管理模块
- ✅ 分类管理模块
- ✅ 基础 API 接口

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目维护者: [Your Name]
- 邮箱: [your.email@example.com]
- 项目地址: [repository-url]

## ⭐ 致谢

感谢所有为本项目做出贡献的开发者们！

---

**Happy Coding! 🎉**
