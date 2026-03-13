#!/bin/bash

# --- Configuration ---
GET_URL="http://localhost:9091/documents"
POST_URL="http://localhost:8085/chainvault/process"
MAX_CONCURRENT=10
INITIAL_DELAY=1
MAX_RETRIES=5
ITERATIONS=1000
LOG_FILE="load_test_results.csv"

# Initialize CSV Log
echo "iteration,id,status_code,timestamp" > "$LOG_FILE"

# 1. Start Global Timer
global_start=$(date +%s)

# 2. Main Iteration Loop
for ((i=1; i<=ITERATIONS; i++)); do
  echo "--- Starting Iteration $i of $ITERATIONS ---"

  # Fetch IDs for this specific run
  ids=$(curl -s "$GET_URL" | jq -r '.[].id')

  # POST Function with Backoff, Jitter, and Logging
  do_post() {
    local iter=$1
    local id=$2
    local delay=$INITIAL_DELAY
    local attempt=1

    while [ "$attempt" -le "$MAX_RETRIES" ]; do
      status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$POST_URL" \
           -H "Content-Type: application/json" \
           -d "{\"docId\": \"$id\"}")

      # Log result to CSV
      echo "$iter,$id,$status_code,$(date +%Y-%m-%d:%H:%M:%S)" >> "$LOG_FILE"

      if [ "$status_code" -eq 429 ]; then
        # Add Jitter
        jitter=$(echo "scale=2; ($RANDOM % 50) / 100" | bc)
        actual_sleep=$(echo "$delay + $jitter" | bc)
        echo "[Iter $iter] ID $id: 429 detected. Retrying in ${actual_sleep}s..."
        sleep "$actual_sleep"
        delay=$((delay * 2))
        attempt=$((attempt + 1))
      else
        return
      fi
    done
  }

  # 3. Parallel Execution for current iteration
  count=0
  for id in $ids; do
    do_post "$i" "$id" &

    ((count++))
    if (( count % MAX_CONCURRENT == 0 )); then
      wait
    fi
  done

  # Wait for all requests in THIS iteration to finish before starting next
  wait
  echo "Iteration $i complete."
done

# 4. Final Summary
global_end=$(date +%s)
echo "------------------------------------"
echo "Full Load Test Complete ($ITERATIONS iterations)"
echo "Total duration: $((global_end - global_start)) seconds"
echo "Results saved to: $LOG_FILE"
echo "------------------------------------"
