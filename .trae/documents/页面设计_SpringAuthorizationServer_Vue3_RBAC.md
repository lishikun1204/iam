# 页面设计说明（桌面优先）

## 全局设计规范
- Layout：桌面端采用“顶部导航 + 左侧菜单 + 右侧内容区”仪表盘结构；内容区使用 CSS Grid（卡片）+ Flex（表单/工具条）混合。
- Breakpoints：
  - ≥1200px：左右留白 24px，表格/表单双列优先
  - 768–1199px：内容区单列为主，表单改为分组折叠
  - <768px：侧边栏抽屉化（如需移动端支持）
- Meta：
  - 默认 Title：IAM 控制台
  - 默认 Description：OAuth2.1 + RBAC 管理后台
  - OG：og:title=IAM 控制台；og:type=website
- Tokens：
  - 背景色：#F6F7FB；内容卡片：#FFFFFF
  - 主色：#1677FF；危险：#FF4D4F；成功：#52C41A
  - 字体：14px 基准，标题 16/18/20
  - Button：Primary/Default/Danger；hover 提亮 6%；disabled 降低不透明度
  - Link：主色，下划线仅在 hover
- 交互：
  - 所有写操作弹确认；成功 toast；失败 toast + 可复制 traceId
  - 表格默认分页；筛选条件变化后回到第 1 页

---

## 1) 登录与授权页（/login, /consent）
### Layout
- 居中卡片布局：页面全屏背景 + 中央 420px 宽卡片；卡片内 Flex 垂直堆叠。

### Meta
- Title：登录 - IAM
- Description：统一身份认证登录

### Page Structure
1. 顶部品牌区
2. 登录卡片 / 授权卡片
3. 底部辅助链接（隐私/条款，可选）

### Sections & Components
- 品牌区
  - Logo（左）+ 产品名（右）
- 登录卡片
  - 输入：username、password
  - 按钮：登录
  - 状态：加载中、错误提示（表单项下方）、账号锁定/禁用提示（卡片顶部 Alert）
- 授权确认(Consent)卡片
  - 客户端信息：应用名、client_id、回调域名摘要
  - scope 列表：勾选（默认全部必选或按策略展示）
  - 按钮：同意（Primary）、拒绝（Default）
  - 安全提示：将授予的权限说明（简短）

---

## 2) 管理控制台（/console/*）
### Layout
- 经典后台：Header（固定）+ Sider（可折叠）+ Main（滚动）。
- Main 内：
  - 顶部工具条（筛选/按钮）用 Flex
  - 列表区用 Table；详情/编辑用 Drawer 或 Modal

### Meta
- Title：控制台 - IAM
- Description：OAuth Client 与 RBAC 管理

### Page Structure
1. Header
2. Sider（菜单）
3. Main（面包屑 + 页面标题 + 工具条 + 内容区）

### Sections & Components
- Header
  - 左：折叠按钮 + 面包屑
  - 右：当前用户（用户名/角色）+ 退出登录
- Sider
  - 菜单项：概览、Clients、用户、角色、权限、授权治理、审计日志、接口文档
  - 权限控制：按 RBAC 过滤不可见项
- 概览（/console）
  - KPI 卡片：用户数、Client 数、近 24h 登录次数、近 24h 授权次数
  - 最近事件：审计日志简表（只读）
- OAuth Client 管理（/console/clients）
  - 筛选：keyword、status
  - 表格列：client_id、名称、scopes、redirect_uris 摘要、状态、更新时间、操作
  - 操作：新增/编辑、禁用、重置 secret（弹窗二次确认；secret 仅展示一次可复制）
- 用户管理（/console/users）
  - 筛选：keyword、status
  - 表格列：username、状态、角色、最近登录、操作
  - 操作：新增/编辑、禁用、重置密码、分配角色（多选穿梭框或多选下拉）
- 角色管理（/console/roles）
  - 表格列：code、name、关联用户数、操作
  - 角色详情 Drawer：权限树（按模块分组：menu/button/api）
- 权限管理（/console/permissions）
  - 表格列：code、name、type、描述、操作
  - 新增/编辑：code 必填且唯一；type 下拉（menu/button/api）
- 授权与令牌治理（/console/*）
  - 查询：userId/clientId/时间范围
  - 列表：授权记录（scopes、创建时间）
  - 操作：撤销授权；吊销 token（输入 jti 或 token 摘要）
- 审计日志（/console/audit）
  - 筛选：action、actor、时间范围
  - 表格列：时间、操作者、动作、目标、详情（可展开）

---

## 3) 接口文档页（/docs）
### Layout
- 使用 Swagger UI 默认双栏（目录 + 详情），外层保持与控制台一致的 Header/Sider。

### Meta
- Title：接口文档 - IAM
- Description：OpenAPI 接口定义与在线调试

### Sections & Components
- 顶部提示条：当前环境、鉴权方式（Bearer Token）、如何获取 token
- Swagger UI：
  - 分组：OAuth、RBAC、Audit
  - Try it out：允许在开发/测试环境启用
  - 统一响应结构与错误码说明（置顶说明块）
