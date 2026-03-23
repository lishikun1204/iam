CREATE TABLE IF NOT EXISTS oauth2_registered_client (
  id varchar(100) NOT NULL,
  client_id varchar(100) NOT NULL,
  client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  client_secret varchar(200) DEFAULT NULL,
  client_secret_expires_at timestamp DEFAULT NULL,
  client_name varchar(200) NOT NULL,
  client_authentication_methods varchar(1000) NOT NULL,
  authorization_grant_types varchar(1000) NOT NULL,
  redirect_uris varchar(4000) DEFAULT NULL,
  post_logout_redirect_uris varchar(4000) DEFAULT NULL,
  scopes varchar(1000) NOT NULL,
  client_settings varchar(2000) NOT NULL,
  token_settings varchar(2000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization (
  id varchar(100) NOT NULL,
  registered_client_id varchar(100) NOT NULL,
  principal_name varchar(200) NOT NULL,
  authorization_grant_type varchar(100) NOT NULL,
  authorized_scopes varchar(1000) DEFAULT NULL,
  attributes bytea DEFAULT NULL,
  state varchar(500) DEFAULT NULL,

  authorization_code_value bytea DEFAULT NULL,
  authorization_code_issued_at timestamp DEFAULT NULL,
  authorization_code_expires_at timestamp DEFAULT NULL,
  authorization_code_metadata bytea DEFAULT NULL,

  access_token_value bytea DEFAULT NULL,
  access_token_issued_at timestamp DEFAULT NULL,
  access_token_expires_at timestamp DEFAULT NULL,
  access_token_metadata bytea DEFAULT NULL,
  access_token_type varchar(100) DEFAULT NULL,
  access_token_scopes varchar(1000) DEFAULT NULL,

  oidc_id_token_value bytea DEFAULT NULL,
  oidc_id_token_issued_at timestamp DEFAULT NULL,
  oidc_id_token_expires_at timestamp DEFAULT NULL,
  oidc_id_token_metadata bytea DEFAULT NULL,

  refresh_token_value bytea DEFAULT NULL,
  refresh_token_issued_at timestamp DEFAULT NULL,
  refresh_token_expires_at timestamp DEFAULT NULL,
  refresh_token_metadata bytea DEFAULT NULL,

  user_code_value bytea DEFAULT NULL,
  user_code_issued_at timestamp DEFAULT NULL,
  user_code_expires_at timestamp DEFAULT NULL,
  user_code_metadata bytea DEFAULT NULL,

  device_code_value bytea DEFAULT NULL,
  device_code_issued_at timestamp DEFAULT NULL,
  device_code_expires_at timestamp DEFAULT NULL,
  device_code_metadata bytea DEFAULT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
  registered_client_id varchar(100) NOT NULL,
  principal_name varchar(200) NOT NULL,
  authorities varchar(1000) NOT NULL,
  PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE IF NOT EXISTS sys_dept (
  id varchar(36) NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(128) NOT NULL,
  parent_id varchar(36) DEFAULT NULL,
  sort_num int NOT NULL,
  leader varchar(64) DEFAULT NULL,
  phone varchar(32) DEFAULT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sys_user (
  id varchar(36) NOT NULL,
  username varchar(64) NOT NULL,
  full_name varchar(64) NOT NULL,
  email varchar(128) DEFAULT NULL,
  phone varchar(32) DEFAULT NULL,
  avatar varchar(512) DEFAULT NULL,
  status varchar(16) NOT NULL,
  dept_id varchar(36) DEFAULT NULL,
  password_hash varchar(255) NOT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS sys_role (
  id varchar(36) NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(64) NOT NULL,
  level_num int NOT NULL,
  data_scope varchar(32) NOT NULL,
  status varchar(16) NOT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS sys_permission (
  id varchar(36) NOT NULL,
  code varchar(128) NOT NULL,
  name varchar(128) NOT NULL,
  type varchar(16) NOT NULL,
  url varchar(512) DEFAULT NULL,
  http_method varchar(16) DEFAULT NULL,
  parent_id varchar(36) DEFAULT NULL,
  sort_num int NOT NULL,
  status varchar(16) NOT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id varchar(36) NOT NULL,
  role_id varchar(36) NOT NULL,
  PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
  role_id varchar(36) NOT NULL,
  permission_id varchar(36) NOT NULL,
  PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS audit_log (
  id varchar(36) NOT NULL,
  actor_user_id varchar(36) DEFAULT NULL,
  action varchar(64) NOT NULL,
  target varchar(128) DEFAULT NULL,
  detail varchar(2000) DEFAULT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);

