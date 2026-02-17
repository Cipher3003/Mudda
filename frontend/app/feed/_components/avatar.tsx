import Image from "next/image";

interface AvatarProps {
  name?: string;
  src?: string;
  size?: "sm" | "md" | "lg";
  className?: string;
}

const sizeClasses = {
  sm: "w-8 h-8 text-xs",
  md: "w-10 h-10 text-sm",
  lg: "w-12 h-12 text-base",
};

export default function Avatar({
  src,
  name = "User",
  size = "md",
  className = "",
}: AvatarProps) {
  const baseStyle = `relative overflow-hidden rounded-full shrink-0 object-cover border border-slate-200 flex items-center justify-center font-bold 
    ${sizeClasses[size]} ${className}`;

  if (src)
    return (
      <div className={baseStyle}>
        <Image
          src={src}
          alt={name}
          fill
          sizes="(max-width: 768px) 10vw, 5vw"
          className="object-cover"
        />
      </div>
    );

  return (
    <div
      className={`${baseStyle} 
      bg-linear-to-br from-slate-200 to-slate-300 text-slate-500`}
    >
      {name.charAt(0).toUpperCase()}
    </div>
  );
}
