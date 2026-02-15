import { useRef, useState } from "react";

export interface CommentInputProps {
  isCompact: boolean;
  autoFocus?: boolean;
  onSubmit?: () => void;
  placeholder?: string;
}

export default function CommentInput(props: CommentInputProps) {
  const { placeholder = "Write a comment" } = props;
  const handleSubmit = () => {
    // TODO: submit comment to API
    if (props.onSubmit) props.onSubmit();
  };

  return (
    <div className={`flex gap-3 ${props.isCompact ? "mb-4" : "mb-8"}`}>
      <div
        className={`rounded-full bg-slate-200 flex items-center justify-center text-slate-500 font-bold shrink-0
        ${props.isCompact ? "w-8 h-8 text-xs" : "w-10 h-10 text-sm"}`}
      >
        ME
      </div>
      <div className="flex-1 mt-0.5">
        <AutoResizeTextarea
          placeholder={placeholder}
          onSubmit={handleSubmit}
          isCompact={props.isCompact}
          autoFocus={props.autoFocus}
        />
      </div>
    </div>
  );
}

export const AutoResizeTextarea = ({
  placeholder,
  onSubmit,
  isCompact,
  autoFocus,
}: any) => {
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const [val, setVal] = useState("");

  const handleChange = (e: any) => {
    setVal(e.target.value);
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
      textareaRef.current.style.height =
        textareaRef.current.scrollHeight + "px";
    }
  };

  const handleKeyDown = (e: any) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      if (val.trim()) {
        onSubmit();
        setVal("");
        if (textareaRef.current) textareaRef.current.style.height = "auto";
      }
    }
  };

  return (
    <div className="relative w-full">
      <textarea
        ref={textareaRef}
        rows={1}
        autoFocus={autoFocus}
        value={val}
        onChange={handleChange}
        onKeyDown={handleKeyDown}
        placeholder={placeholder}
        className={`w-full border-b border-slate-200 focus:outline-none focus:border-blue-600 transition-colors bg-transparent placeholder:text-slate-400 text-slate-700 resize-none overflow-hidden
          ${isCompact ? "py-1.5 pr-8 text-sm min-h-8" : "py-2 pr-12 text-base min-h-10"}
        `}
        style={{ height: "auto" }}
      />
      <button
        onClick={() => {
          onSubmit();
          setVal("");
        }}
        className={`absolute right-0 text-blue-600 font-bold hover:text-blue-800 disabled:text-slate-300 uppercase
          ${isCompact ? "bottom-2 text-xs" : "bottom-3 text-sm"}
        `}
        disabled={!val.trim()}
      >
        Post
      </button>
    </div>
  );
};
