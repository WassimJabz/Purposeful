// By default, server components
// Since we want interaction with the user,
// we need to use client components
// see https://beta.nextjs.org/docs/rendering/server-and-client-components#when-to-use-server-vs-client-components
"use client";
import Login from "@/components/Login";
import { TextField } from "@mui/material";
export default function page() {
  return (
    <div>
      <Login />
    </div>
  );
}
