#!/usr/bin/env bash
# End-to-end smoke test against a running :server instance -- the same
# curl checks that were hand-typed and re-typed against the live Cloud Run
# URL to verify deploys during development. Scripted so any agent (or CI)
# runs the identical check instead of reconstructing it from memory, and so
# a deploy can verify itself actually works rather than just that it
# started (an agent -- or a human -- declaring success because a process
# came up, without checking it does the right thing, is exactly the kind of
# gap this closes).
#
# Usage: server/scripts/smoke-test.sh [base_url]
#   base_url defaults to the live Cloud Run URL.
set -uo pipefail

BASE_URL="${1:-https://cryptotracker-server-560504442386.us-central1.run.app}"
FAILURES=0

check() {
  local desc="$1" path="$2" expected_status="${3:-200}"
  local response status body
  response=$(curl -sS -w '\n%{http_code}' "${BASE_URL}${path}")
  status="${response##*$'\n'}"
  body="${response%$'\n'*}"
  if [[ "$status" != "$expected_status" ]]; then
    echo "FAIL: $desc ($path) -> expected HTTP $expected_status, got $status"
    echo "  body: $body"
    FAILURES=$((FAILURES + 1))
  else
    echo "OK:   $desc ($path) -> HTTP $status"
  fi
}

echo "Smoke-testing :server at $BASE_URL"
echo

check "coin list"                "/api/v1/coins?limit=2"
check "coin markets"             "/api/v1/coins/bitcoin/markets?limit=2"
check "coin history (1d)"        "/api/v1/coins/bitcoin/history?range=1d"
check "coin history (1y)"        "/api/v1/coins/bitcoin/history?range=1y"
check "unknown coin 404s"        "/api/v1/coins/not-a-real-coin/history" 404
check "invalid range 400s"       "/api/v1/coins/bitcoin/history?range=nonsense" 400

echo
if [[ "$FAILURES" -gt 0 ]]; then
  echo "$FAILURES check(s) failed."
  exit 1
fi
echo "All checks passed."
