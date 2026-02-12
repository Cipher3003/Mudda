export default function dateToTimeString(createdAt: string) {
  const date = new Date(createdAt);
  const diff = Math.floor((Date.now() - date.getTime()) / 1000); // difference in seconds

  if (diff < 60) return `${diff}s ago`;
  if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
  if (diff < 604800) return `${Math.floor(diff / 86400)}d ago`;

  return date.toLocaleDateString("en-IN");
}
