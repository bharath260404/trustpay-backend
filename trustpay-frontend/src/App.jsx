import { useState } from 'react'
import './App.css'
import UrlScanner from './components/UrlScanner'
import QrScanner from './components/QrScanner'

/**
 * Main App Component
 * 
 * Contains tab navigation to switch between URL Scanner and QR Scanner
 */
function App() {
  const [activeTab, setActiveTab] = useState('url');

  return (
    <div className="App">
      {/* App Header */}
      <header className="App-header">
        <h1>🛡️ TrustPay - Fraud Prevention</h1>
        <p>AI-Powered UPI Security Suite</p>
      </header>

      {/* Tab Navigation */}
      <div className="tab-container">
        <button 
          className={`tab ${activeTab === 'url' ? 'active' : ''}`}
          onClick={() => setActiveTab('url')}
        >
          🔍 URL Scanner
        </button>
        <button 
          className={`tab ${activeTab === 'qr' ? 'active' : ''}`}
          onClick={() => setActiveTab('qr')}
        >
          📱 QR Scanner
        </button>
      </div>

      {/* Main Content Area */}
      <main className="App-main">
        {activeTab === 'url' && <UrlScanner />}
        {activeTab === 'qr' && <QrScanner />}
      </main>
    </div>
  )
}

export default App
