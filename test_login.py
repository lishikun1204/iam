from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    page.on("console", lambda msg: print(f"[console:{msg.type}] {msg.text}"))

    def on_response(resp):
        url = resp.url
        if "/oauth2/token" in url:
            try:
                body = resp.text()
            except Exception:
                body = "<no-body>"
            body_preview = body[:800].replace("\n", "\\n")
            print(f"[oauth2/token] status={resp.status} url={url} body={body_preview}")

    page.on("response", on_response)
    print("Navigating to http://localhost:5174/ ...")
    page.goto('http://localhost:5174/')
    
    # Wait for network idle to ensure the app has loaded
    page.wait_for_load_state('networkidle')
    print("Page loaded. Title:", page.title())
    
    # Check if we are on the login page by looking for the login button or the OAuth redirect
    if "oauth" in page.url or "login" in page.url.lower():
        print(f"Current URL is: {page.url}, which looks like a login/oauth flow.")
    else:
        print(f"Current URL is: {page.url}")
        
    page.screenshot(path='login_test.png', full_page=True)
    print("Saved screenshot to login_test.png")
    
    # Let's interact with the login flow
    # Look for a login button and click it
    try:
        print("Looking for login button...")
        login_button = page.locator('button', has_text="登录")
        if login_button.count() > 0:
            login_button.first.click()
            page.wait_for_load_state('networkidle')
            print("Clicked login button. New URL:", page.url)
            page.screenshot(path='after_login_click.png', full_page=True)
            
            # Now we are on the Spring Authorization Server login page
            # Fill in the credentials
            print("Filling in login credentials...")
            page.fill('input[name="username"]', 'admin')
            page.fill('input[name="password"]', 'Admin@123')
            page.screenshot(path='before_submit.png', full_page=True)
            
            # Click the sign in button
            # Need to use wait_for_load_state carefully or expect_navigation
            with page.expect_navigation():
                page.click('button[type="submit"]')
            page.wait_for_load_state('networkidle')
            
            print("After submit, New URL:", page.url)
            page.screenshot(path='after_submit.png', full_page=True)
            
            # We might be redirected back to the app, or to an authorization consent page
            if "oauth2/authorize" in page.url or "consent" in page.url:
                print("Checking for consent page...")
                # Try to click the consent button if it exists
                consent_button = page.locator('button[id="submit-consent"]')
                if consent_button.count() > 0:
                    consent_button.click()
                    page.wait_for_load_state('networkidle')
                    print("Clicked consent. New URL:", page.url)
            
            page.wait_for_timeout(1500)
            
            page.screenshot(path='final_state.png', full_page=True)
            print("Login flow complete.")
            
        else:
            print("Login button not found.")
    except Exception as e:
        print("Error interacting with login:", e)
        
    browser.close()
