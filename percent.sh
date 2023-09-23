#!/bin/sh
uncompleted=$(find patches -type f | wc -l)
completed=$(find src/main/java/net/feltmc/neoforge/patches/mixin -type f | wc -l)
total=$((completed+uncompleted))
percent=$((100*completed/total))

echo "total: $total"
echo "uncompleted: $uncompleted"
echo "completed: $completed"
echo "percent: $percent"
