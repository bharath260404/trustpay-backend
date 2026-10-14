import './App.css'
import UrlScanner from './components/UrlScanner'

/**
 * Main App Component
 * 
 * This is the root component of your TrustPay application.
 * It contains the header and the main feature (URL Scanner).
 */
function App() {
  return (
    <div className="App">
      {/* App Header */}
      <header className="App-header">
        <h1>🛡️ TrustPay - Fraud Prevention</h1>
        <p>AI-Powered UPI Security Suite</p>
      </header>

      {/* Main Content Area */}
      <main className="App-main">
        {/* URL Scanner Component */}
        <UrlScanner />
      </main>
    </div>
  )
}

export default App
