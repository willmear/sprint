"use client";

import Image from "next/image";

export function ProfileAvatar({
  avatarUrl,
  displayName,
  size = 56,
}: {
  avatarUrl?: string | null;
  displayName: string;
  size?: number;
}) {
  if (avatarUrl) {
    return (
      <Image
        alt={displayName}
        className="rounded-full border border-line object-cover"
        height={size}
        loader={({ src }) => src}
        src={avatarUrl}
        unoptimized
        width={size}
      />
    );
  }

  return (
    <div
      className="flex items-center justify-center rounded-full border border-line bg-sand font-semibold text-ink"
      style={{ height: size, width: size }}
    >
      {displayName.slice(0, 1).toUpperCase()}
    </div>
  );
}
