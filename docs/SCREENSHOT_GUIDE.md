# 📸 Screenshot Checklist for TrustPay Portfolio

## 🎯 Required Screenshots (5 total)

### Screenshot 1: URL Scanner Interface (Empty State)
**What to capture:**
- Clean interface with empty input field
- "🔍 URL Scanner" header
- Purple gradient background
- Input field placeholder text
- "Scan URL" button

**How to take:**
1. Open browser: http://localhost:5173
2. Make sure the page looks clean (no results showing)
3. Press `Windows + Shift + S`
4. Select rectangular area
5. Capture the entire scanner interface
6. Save as: `url-scanner-interface.png`

---

### Screenshot 2: Safe URL Result (Google.com)
**What to capture:**
- Input field with "https://www.google.com"
- Green checkmark ✅
- "URL Appears Safe" message
- Risk Level: SAFE (green badge)
- Risk Score: 0/100 or low score
- Green progress bar
- Security indicators (if any)

**How to take:**
1. Make sure backend is running (docker-compose up -d)
2. In browser: http://localhost:5173
3. Type: `https://www.google.com`
4. Click "Scan URL"
5. Wait for green results to appear
6. Press `Windows + Shift + S`
7. Capture the entire result card
8. Save as: `safe-url-result.png`

---

### Screenshot 3: Suspicious URL Result
**What to capture:**
- Input field with suspicious URL
- Warning icon ⚠️ or red icon
- "URL is Suspicious" message
- Risk Level: WARNING or BLOCKED (yellow/red badge)
- Higher risk score (above 50)
- Risk factors list
- Recommendation message

**Test URLs to try:**
- `http://test-phishing.tk`
- `http://paypal-verify-account.com`
- `http://192.168.1.1/login`

**How to take:**
1. In browser: http://localhost:5173
2. Type one of the test URLs above
3. Click "Scan URL"
4. Wait for warning results
5. Press `Windows + Shift + S`
6. Capture the entire result card
7. Save as: `suspicious-url-result.png`

---

### Screenshot 4: Docker Containers Running
**What to capture:**
- Terminal/PowerShell window
- Output of `docker ps` command
- Show both containers: trustpay-backend and trustpay-postgres
- Status showing "Up" with ports

**How to take:**
1. Open PowerShell
2. Run: `docker ps --filter "name=trustpay"`
3. Make sure output shows both containers running
4. Press `Windows + Shift + S`
5. Capture the terminal output
6. Save as: `docker-containers.png`

**Expected output should show:**
```
NAMES               STATUS              PORTS
trustpay-backend    Up X minutes        0.0.0.0:8081->8080/tcp
trustpay-postgres   Up X minutes        0.0.0.0:5432->5432/tcp
```

---

### Screenshot 5: API Response (Optional but Impressive)
**What to capture:**
- Browser showing raw JSON API response
- Shows technical knowledge

**How to take:**
1. Open browser
2. Go to: http://localhost:8081/api/v1/health
3. You'll see JSON response
4. Press `Windows + Shift + S`
5. Capture the JSON output
6. Save as: `api-response.png`

---

## 📁 Where to Save Screenshots

Create this folder structure:
```
C:\Users\Bharath\trustpay-backend\docs\screenshots\
```

Save all screenshots there:
- `url-scanner-interface.png`
- `safe-url-result.png`
- `suspicious-url-result.png`
- `docker-containers.png`
- `api-response.png` (optional)

---

## 🎨 Pro Tips for Better Screenshots

### For Browser Screenshots:
1. **Zoom to 100%** (Ctrl + 0) for crisp images
2. **Hide bookmarks bar** (Ctrl + Shift + B) for cleaner look
3. **Use full browser width** - drag browser window wide
4. **Remove unnecessary tabs** - close other tabs for clean screenshot
5. **Wait for animations** - let loading spinners finish

### For Terminal Screenshots:
1. **Increase font size** - Make text more readable
2. **Use full width** - Stretch terminal window wide
3. **Clean prompt** - Make sure no error messages showing
4. **Dark theme** - Looks more professional

### General Tips:
1. **Natural lighting** - If showing on screen, good lighting helps
2. **No sensitive info** - Make sure no personal data visible
3. **High resolution** - Don't compress too much
4. **Consistent style** - Use same browser zoom for all screenshots

---

## ✅ Quick Checklist

Before taking screenshots:

- [ ] Docker Desktop running
- [ ] Backend running (docker-compose up -d)
- [ ] Frontend running (npm run dev)
- [ ] Browser at 100% zoom
- [ ] URL bar looks clean
- [ ] No error messages showing
- [ ] Tested safe URL (google.com)
- [ ] Tested suspicious URL (.tk domain)
- [ ] Screenshots folder created

---

## 🚀 Commands Quick Reference

```powershell
# Start Docker
# (Open Docker Desktop GUI)

# Start Backend
cd C:\Users\Bharath\trustpay-backend
docker-compose up -d

# Check Docker status
docker ps --filter "name=trustpay"

# Start Frontend (if not running)
cd C:\Users\Bharath\trustpay-backend\trustpay-frontend
npm run dev

# Create screenshots folder
mkdir -p docs\screenshots
```

---

## 📤 After Taking Screenshots

Once you have all screenshots:

1. **Review them** - Make sure they're clear and professional
2. **Add to Git:**
```powershell
cd C:\Users\Bharath\trustpay-backend
git add docs/screenshots/
git commit -m "docs: Add project screenshots for portfolio"
git push
```

3. **Update README.md** - Replace placeholder image paths with real ones

---

## 🎯 What Recruiters Look For

Good screenshots show:
✅ **Professional UI** - Clean, modern design
✅ **Working features** - Real functionality, not mockups
✅ **Technical depth** - Docker, APIs, JSON responses
✅ **Attention to detail** - No errors, clean interface
✅ **Full stack skills** - Frontend + Backend + DevOps

---

**When you're ready**, type "screenshots taken" and I'll help you add them to Git and update the README!

Good luck! 📸✨
