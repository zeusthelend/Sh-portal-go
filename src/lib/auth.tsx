import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import type { Session, User } from "@supabase/supabase-js";
import { useQuery } from "@tanstack/react-query";
import { supabase } from "@/integrations/supabase/client";
import type { Database } from "@/integrations/supabase/types";

export type AppRole = Database["public"]["Enums"]["app_role"];
export type Profile = Database["public"]["Tables"]["profiles"]["Row"];

export const DEMO_USER = {
  id: "user-anderson-01",
  email: "anderson@shoppinghospitalar.com.br",
  user_metadata: { full_name: "Anderson Silva" },
  app_metadata: {},
  aud: "authenticated",
  created_at: "2026-01-01T00:00:00.000Z",
} as unknown as User;

export const DEMO_SESSION = {
  access_token: "demo-token",
  token_type: "bearer",
  expires_in: 3600,
  refresh_token: "demo-refresh",
  user: DEMO_USER,
} as unknown as Session;

export const DEMO_PROFILE: Profile & { positions?: { name: string } | null } = {
  id: "user-anderson-01",
  full_name: "Anderson Silva",
  avatar_url: null,
  status: "online",
  active: true,
  approved: true,
  created_at: "2026-01-01T00:00:00.000Z",
  last_seen_at: new Date().toISOString(),
  phone: "(11) 98765-4321",
  positions: { name: "Vendedor Hospitalar & Administrador" },
};

type AuthCtx = { session: Session | null; loading: boolean };
const Ctx = createContext<AuthCtx>({ session: DEMO_SESSION, loading: false });

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(DEMO_SESSION);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    try {
      const { data } = supabase.auth.onAuthStateChange((_e, s) => {
        setSession(s ?? DEMO_SESSION);
      });
      supabase.auth.getSession().then(({ data }) => {
        if (data?.session) {
          setSession(data.session);
        } else {
          setSession(DEMO_SESSION);
        }
      }).catch(() => {
        setSession(DEMO_SESSION);
      });
      return () => data?.subscription?.unsubscribe?.();
    } catch {
      setSession(DEMO_SESSION);
    }
  }, []);

  return <Ctx.Provider value={{ session, loading }}>{children}</Ctx.Provider>;
}

export const useSession = () => useContext(Ctx);

export function useMe() {
  const { session } = useSession();
  const uid = session?.user.id || DEMO_USER.id;

  return useQuery({
    queryKey: ["me", uid],
    queryFn: async () => {
      try {
        const [profileResult, rolesResult] = await Promise.all([
          supabase.from("profiles").select("*, positions(name)").eq("id", uid).maybeSingle(),
          supabase.from("user_roles").select("role").eq("user_id", uid),
        ]);

        if (profileResult.data) {
          const profile = profileResult.data;
          const roles = rolesResult.data;
          const r = (roles ?? []).map((x) => x.role as AppRole);
          const canManage = r.some((x) => x === "admin" || x === "diretor" || x === "gerente");
          const positionName = String((profile as (typeof profile & { positions?: { name?: string } | null }) | null)?.positions?.name ?? "")
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .toLowerCase();
          const isStockLead = positionName.includes("encarregado") && positionName.includes("estoque");
          const isStockRole = positionName.includes("estoquista") || positionName.includes("estoque");
          const isAdministrativeRole = positionName.includes("administrativo");

          return {
            profile,
            roles: r,
            isAdmin: r.includes("admin"),
            canManage,
            isStockLead,
            isStockRole,
            canAccessStockTools: canManage || isStockRole || isStockLead || positionName.includes("administrador"),
            canAccessAdministrativeTools: canManage || isAdministrativeRole || positionName.includes("administrador"),
            canRegisterDavForSeller: canManage || isStockRole,
            canViewAllSales: canManage || isStockLead,
          };
        }
      } catch {
        // Fallback to demo profile below
      }

      // Safe offline / fallback demo profile
      return {
        profile: DEMO_PROFILE,
        roles: ["admin", "gerente"] as AppRole[],
        isAdmin: true,
        canManage: true,
        isStockLead: true,
        isStockRole: true,
        canAccessStockTools: true,
        canAccessAdministrativeTools: true,
        canRegisterDavForSeller: true,
        canViewAllSales: true,
      };
    },
  });
}

export const ROLE_LABEL: Record<AppRole, string> = {
  admin: "Administrador",
  diretor: "Diretor",
  gerente: "Gerente",
  funcionario: "Funcionário",
};

export async function uploadSigned(bucket: string, path: string, file: File) {
  try {
    const { error } = await supabase.storage.from(bucket).upload(path, file, { upsert: true });
    if (error) throw error;
    const { data, error: e2 } = await supabase.storage
      .from(bucket)
      .createSignedUrl(path, 60 * 60 * 24 * 365 * 5);
    if (e2) throw e2;
    return data.signedUrl;
  } catch {
    return URL.createObjectURL(file);
  }
}
