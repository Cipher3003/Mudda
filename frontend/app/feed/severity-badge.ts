export default function getSeverityBadge(severity: string | number) {
  const sev = String(severity).toLowerCase();

  if (sev === "high" || sev === "critical" || Number(sev) > 7) {
    return { bg: "bg-red-100", text: "text-red-700", icon: "text-red-600" };
  }

  if (sev === "medium" || (Number(sev) > 3 && Number(sev) <= 7)) {
    return {
      bg: "bg-orange-100",
      text: "text-orange-800",
      icon: "text-orange-600",
    };
  }

  return { bg: "bg-slate-100", text: "text-slate-600", icon: "text-slate-500" };
}
