"use client";

import { ArrowLeft } from "lucide-react";

export default function BackButton() {
  return (
    <button
      onClick={() => window.history.back()}
      className="p-2 hover:bg-slate-100 rounded-full transition-colors"
    >
      <ArrowLeft size={20} className="text-slate-600" />
    </button>
  );
}
