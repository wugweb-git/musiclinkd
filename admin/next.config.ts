import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "export",
  images: {
    // next/image's optimization API needs a server; static export has none.
    unoptimized: true,
  },
};

export default nextConfig;
