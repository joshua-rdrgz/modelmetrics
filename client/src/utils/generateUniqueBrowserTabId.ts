export function generateUniqueBrowserTabId() {
  const existingTabId = sessionStorage.getItem('stopwatchTabId');
  if (existingTabId) {
    return existingTabId;
  }

  const newTabId = crypto.randomUUID();
  sessionStorage.setItem('stopwatchTabId', newTabId);
  return newTabId;
}
