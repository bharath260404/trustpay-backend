import { useState, useRef } from 'react';
import { scanQRCode } from '../services/api';
import './QrScanner.css';

/**
 * QrScanner Component - Scan QR codes using camera or file upload
 */
function QrScanner() {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [scanMode, setScanMode] = useState('upload'); // 'upload' or 'camera'
  const fileInputRef = useRef(null);
  const videoRef = useRef(null);

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setLoading(true);
    setError('');
    setResult(null);

    try {
      const response = await scanQRCode(file);
      if (response.success) {
        setResult(response.data);
      } else {
        setError(response.error || 'Scan failed');
      }
    } catch (err) {
      setError('Scan error. Try uploading the image.');
    } finally {
      setLoading(false);
    }
  };

  const startCamera = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } });
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        videoRef.current.play();
      }
    } catch (err) {
      setError('Camera access denied. Please allow camera access or use file upload.');
    }
  };

  return (
    <div className="qr-scanner">
      <div className="scanner-header">
        <h2>📱 QR Scanner</h2>
        <p>Detect fake QR codes and verify business entities</p>
      </div>

      {/* Scan Mode Toggle */}
      <div className="mode-toggle">
        <button 
          className={scanMode === 'upload' ? 'active' : ''}
          onClick={() => setScanMode('upload')}
        >
          📁 Upload Image
        </button>
        <button 
          className={scanMode === 'camera' ? 'active' : ''}
          onClick={() => { setScanMode('camera'); startCamera(); }}
        >
          📷 Camera Scan
        </button>
      </div>

      {/* Upload Mode */}
      {scanMode === 'upload' && (
        <div className="upload-section">
          <input 
            type="file" 
            accept="image/*" 
            ref={fileInputRef}
            onChange={handleFileUpload}
            style={{ display: 'none' }}
          />
          <button 
            className="upload-btn"
            onClick={() => fileInputRef.current?.click()}
            disabled={loading}
          >
            📁 Select QR Image
          </button>
          <p className="upload-hint">Upload a screenshot or photo of the QR code</p>
        </div>
      )}

      {/* Camera Mode */}
      {scanMode === 'camera' && (
        <div className="camera-section">
          <video 
            ref={videoRef} 
            className="camera-preview"
            autoPlay
            playsInline
          />
          <p className="camera-hint">Point camera at QR code</p>
        </div>
      )}

      {/* Loading */}
      {loading && (
        <div className="loading">
          <div className="spinner"></div>
          <p>Analyzing QR code...</p>
        </div>
      )}

      {/* Results */}
      {result && !loading && (
        <div className={`result-card ${result.isFake ? 'fake' : 'safe'}`}>
          <div className="result-header">
            <div className={`status-icon ${result.isFake ? 'fake' : 'safe'}`}>
              {result.isFake ? '🚨' : (result.fraudStatus === 'FAKE' ? '⚠️' : '✅')}
            </div>
            <div>
              <h3>{result.businessFound ? result.businessName : (result.isFake ? 'FAKE QR DETECTED!' : 'No Business Found')}</h3>
              <p>UPI ID: {result.payeeVpa || 'Not found'}</p>
            </div>
          </div>

          {/* Fraud Status */}
          <div className="fraud-status">
            <span className={`status-badge ${result.fraudStatus?.toLowerCase()}`}>
              {result.fraudStatus || 'UNKNOWN'}
            </span>
            <span className="risk-level">Risk: {Math.round((result.riskScore || 0) * 100)}%</span>
          </div>

          {/* Business Info */}
          {result.businessFound && (
            <div className="business-info">
              <h4>🏢 Business Details</h4>
              <p><strong>Business:</strong> {result.businessName}</p>
              <p><strong>Owner:</strong> {result.businessOwner}</p>
              <p><strong>Type:</strong> {result.businessType}</p>
              <p><strong>Location:</strong> {result.businessLocation}</p>
              <p><strong>Verified:</strong> {result.businessVerified ? '✅ Yes' : '❌ No'}</p>
            </div>
          )}

          {/* Fraud Reasons */}
          {result.fraudReasons && result.fraudReasons.length > 0 && (
            <div className="fraud-reasons">
              <h4>⚠️ Fraud Indicators</h4>
              <ul>
                {result.fraudReasons.map((reason, index) => (
                  <li key={index}>• {reason}</li>
                ))}
              </ul>
            </div>
          )}

          {/* Recommendation */}
          <div className="recommendation">
            <strong>💡 Recommendation:</strong> {result.recommendation}
          </div>
        </div>
      )}
    </div>
  );
}

export default QrScanner;
