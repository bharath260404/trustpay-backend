import { useState } from 'react';
import { scanUrl } from '../services/api';
import './UrlScanner.css';

/**
 * UrlScanner Component
 * 
 * This component allows users to check if a URL is safe or potentially fraudulent.
 * It demonstrates form handling, API calls, and conditional rendering in React.
 */
function UrlScanner() {
  // ═══════════════════════════════════════════════════════════════════
  // STATE MANAGEMENT
  // ═══════════════════════════════════════════════════════════════════
  
  // 1. url: Stores what the user types in the input field
  const [url, setUrl] = useState('');
  
  // 2. loading: Tracks if we're currently waiting for the API response
  //    - true: Show loading spinner
  //    - false: Show normal UI
  const [loading, setLoading] = useState(false);
  
  // 3. result: Stores the API response after scanning
  //    - null: No scan done yet
  //    - object: Contains scan results (isSafe, riskLevel, indicators, etc.)
  const [result, setResult] = useState(null);
  
  // 4. error: Stores any error message if the API fails
  const [error, setError] = useState('');

  // ═══════════════════════════════════════════════════════════════════
  // FORM SUBMISSION HANDLER
  // ═══════════════════════════════════════════════════════════════════
  
  /**
   * handleSubmit - Called when user clicks "Scan URL" button
   * 
   * @param {Event} e - The form submit event
   */
  const handleSubmit = async (e) => {
    // IMPORTANT: Prevent the browser from refreshing the page
    // By default, forms cause a page reload. We want to handle it with JavaScript instead.
    e.preventDefault();
    
    // Validation: Don't scan if input is empty
    if (!url.trim()) {
      setError('Please enter a URL to scan');
      return;
    }
    
    // Reset previous results and errors
    setError('');
    setResult(null);
    
    // Set loading to true (this will show the spinner)
    setLoading(true);
    
    try {
      // Call our API service (remember api.js?)
      // await pauses here until the API responds
      const response = await scanUrl(url);
      
      // If API call succeeded, process and store the result
      if (response.success) {
        const processedResult = processResult(response.data);
        setResult(processedResult);
      } else {
        // API returned an error
        setError(response.error || 'Failed to scan URL');
      }
    } catch (err) {
      // Network error or unexpected problem
      setError('Network error. Please check if the backend is running.');
      console.error('URL scan error:', err);
    } finally {
      // finally block ALWAYS runs, whether try succeeded or failed
      // This ensures we stop the loading spinner either way
      setLoading(false);
    }
  };

  // ═══════════════════════════════════════════════════════════════════
  // HELPER FUNCTIONS
  // ═══════════════════════════════════════════════════════════════════
  
  /**
   * Process backend response to match frontend expectations
   * Backend returns: riskLevel = 'SAFE' | 'WARNING' | 'BLOCKED'
   * Backend returns: riskScore = 0.0 to 1.0
   */
  const processResult = (data) => {
    // Determine if URL is safe based on backend's riskLevel
    const isSafe = data.riskLevel === 'SAFE';
    
    // Convert risk score from 0.0-1.0 to 0-100 for display
    const displayScore = Math.round(data.riskScore * 100);
    
    // Map backend risk indicators to frontend format
    const indicators = data.riskFactors?.map((factor) => ({
      type: data.riskLevel === 'SAFE' ? 'POSITIVE' : 'NEGATIVE',
      description: factor
    })) || [];
    
    return {
      ...data,
      isSafe,
      riskScore: displayScore,
      url: data.inputUrl,
      indicators,
      recommendation: data.warningMessage || (isSafe ? 
        'This URL appears safe to visit.' : 
        'Exercise caution when visiting this URL.')
    };
  };
  
  /**
   * Returns a CSS class name based on the risk level
   * Backend uses: SAFE, WARNING, BLOCKED
   */
  const getRiskClass = (riskLevel) => {
    const riskMap = {
      'SAFE': 'risk-low',
      'WARNING': 'risk-medium',
      'BLOCKED': 'risk-critical'
    };
    return riskMap[riskLevel] || 'risk-unknown';
  };

  // ═══════════════════════════════════════════════════════════════════
  // RENDER (JSX)
  // ═══════════════════════════════════════════════════════════════════
  
  return (
    <div className="url-scanner">
      {/* Header */}
      <div className="scanner-header">
        <h2>🔍 URL Scanner</h2>
        <p>Check if a URL is safe before clicking</p>
      </div>

      {/* Form for URL input */}
      <form onSubmit={handleSubmit} className="scanner-form">
        <div className="input-group">
          <input
            type="text"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            placeholder="Enter URL (e.g., https://example.com)"
            className="url-input"
            disabled={loading}
          />
          <button 
            type="submit" 
            className="scan-button"
            disabled={loading}
          >
            {loading ? 'Scanning...' : 'Scan URL'}
          </button>
        </div>
      </form>

      {/* Error Message - Only shows if error exists */}
      {error && (
        <div className="alert alert-error">
          ⚠️ {error}
        </div>
      )}

      {/* Loading Spinner - Only shows while loading is true */}
      {loading && (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Analyzing URL security...</p>
        </div>
      )}

      {/* Results Display - Only shows if we have results AND not loading */}
      {result && !loading && (
        <div className={`result-card ${result.isSafe ? 'safe' : 'unsafe'}`}>
          {/* Status Header */}
          <div className="result-header">
            <div className={`status-icon ${result.isSafe ? 'safe' : 'unsafe'}`}>
              {result.isSafe ? '✅' : '⚠️'}
            </div>
            <div>
              <h3>{result.isSafe ? 'URL Appears Safe' : 'URL is Suspicious'}</h3>
              <p className="scanned-url">{result.url}</p>
            </div>
          </div>

          {/* Risk Level Badge */}
          <div className="risk-badge-container">
            <span className="label">Risk Level:</span>
            <span className={`risk-badge ${getRiskClass(result.riskLevel)}`}>
              {result.riskLevel}
            </span>
          </div>

          {/* Risk Score */}
          <div className="risk-score">
            <div className="score-label">Risk Score</div>
            <div className="score-value">{result.riskScore}/100</div>
            <div className="score-bar">
              <div 
                className="score-fill"
                style={{ width: `${result.riskScore}%` }}
              ></div>
            </div>
          </div>

          {/* Security Indicators */}
          {result.indicators && result.indicators.length > 0 && (
            <div className="indicators-section">
              <h4>🛡️ Security Indicators</h4>
              <ul className="indicators-list">
                {result.indicators.map((indicator, index) => (
                  <li key={index} className={`indicator ${indicator.type}`}>
                    <span className="indicator-icon">
                      {indicator.type === 'POSITIVE' ? '✓' : 
                       indicator.type === 'NEGATIVE' ? '✗' : 'ℹ'}
                    </span>
                    <span className="indicator-text">{indicator.description}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Recommendations */}
          {result.recommendation && (
            <div className="recommendation">
              <strong>💡 Recommendation:</strong> {result.recommendation}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default UrlScanner;
