"use client";

import { useEffect, useState } from "react";
import { formatToRelativeTime } from "../format-dates";

export default function TimeAgo({ timestamp }: { timestamp: string }) {
  const [time, setTime] = useState<string | null>(null);

  useEffect(() => {
    function update() {
      setTime(formatToRelativeTime(timestamp));
    }

    update();
    const interval = setInterval(update, 60000); // Update every minute
    return () => clearInterval(interval);
  }, [timestamp]);

  if (!time) return null;
  return <time dateTime={timestamp}>{time}</time>;
}
