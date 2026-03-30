# IAM (Spring Authorization Server + Vue3)

## 先决条件
- JDK 21+
- Maven 3.9+
- Node.js 24+
- 已启动 Redis（推荐使用 `docker-compose up -d`，需要本机已安装 Docker Desktop / Docker Engine）

## 启动后端（dev）
```bash
cd backend
mvn -pl iam-server spring-boot:run -Dspring-boot.run.profiles=dev
```

如果你没有 Docker，也可以自行安装 Redis 并保持 `localhost:6379` 可用。

后端默认：
- Base URL: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`（dev）
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Knife4j: `http://localhost:8080/doc.html`

默认初始化账号：
- 用户名：`admin`
- 密码：`Admin@123`

## 启动前端
```bash
cd frontend
pnpm install
pnpm dev
```

可选：如果你后端不是 `http://localhost:8080`，请设置环境变量 `VITE_AUTH_BASE_URL`（未设置时默认使用 `VITE_SERVICE_BASE_URL`）。

前端默认：
- `http://localhost:5173`

## OAuth2.1 授权码登录
- 前端点击“登录”会跳转到后端 `/oauth2/authorize`
- 首次会进入后端表单登录页，登录成功后回跳前端 `/oauth/callback`
- 前端使用 PKCE 交换 token，拿到 `access_token` 后调用 `/api/me` 拉取权限并生成动态路由
- 登录页额外提供“用户名密码登录”（`grant_type=password`，自定义扩展）
- 前端“退出登录”会调用后端 `/api/auth/logout`，服务端撤销当前 token 并清理授权记录
- 参考时序图：`docs/OAuth2.1授权码PKCE时序图.md`
- JWK 配置（避免重启导致历史 token 失效）：`docs/JWK密钥配置.md`

## 代码质量
后端：
```bash
cd backend
mvn -pl iam-server -am verify
```

前端：
```bash
cd frontend
pnpm run check
pnpm run lint
```
