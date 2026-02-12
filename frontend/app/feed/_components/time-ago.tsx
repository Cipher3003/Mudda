"use client";

import { useEffect, useState } from "react";
import formatTimeAgo from "../format-time-ago";

export default function TimeAgo({ timestamp }: { timestamp: string }) {
  const [time, setTime] = useState<string | null>(null);

  useEffect(() => {
    function update() {
      setTime(formatTimeAgo(timestamp));
    }

    update();
    const interval = setInterval(update, 60000); // Update every minute
    return () => clearInterval(interval);
  }, [timestamp]);

  if (!time) return null;
  return <time dateTime={timestamp}>{time}</time>;
}
