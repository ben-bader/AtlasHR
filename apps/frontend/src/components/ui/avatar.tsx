import * as React from "react";
import { cn } from "@/lib/utils/cn";

export type AvatarProps = React.HTMLAttributes<HTMLDivElement> & {
  name?: string;
  image?: string;
};

export const Avatar = React.forwardRef<HTMLDivElement, AvatarProps>(
  ({ className, name, image, ...props }, ref) => {
    const initials = name
      ?.split(" ")
      .map((part) => part[0])
      .join("")
      .slice(0, 2)
      .toUpperCase();

    return (
      <div
        ref={ref}
        className={cn(
          "inline-flex h-11 w-11 items-center justify-center rounded-2xl bg-brand-100 text-sm font-semibold text-brand-700",
          className
        )}
        {...props}
      >
        {image ? <img src={image} alt={name || "avatar"} className="h-full w-full rounded-2xl object-cover" /> : initials || "HR"}
      </div>
    );
  }
);
Avatar.displayName = "Avatar";
