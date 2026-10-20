import { livePresence } from "@/components/brand";
import { createFileRoute } from "@tanstack/react-router";
import { useServerFn } from "@tanstack/react-start";
import { useQuery } from "@tanstack/react-query";
import {
  Bot,
  Building2,
  Download,
  Mail,
  MapPin,
  MessageCircle,
  Phone,
  PhoneCall,
  Search,
  Sparkles,
  UserCheck,
  UserPlus,
  Users,
} from "lucide-react";
import { useState } from "react";
import { useNavigate } from "@tanstack/react-router";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import { recommendTeam } from "@/lib/team-recommendation.functions";
import { useMe } from "@/lib/auth";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { PageHeader, StatusDot, initials, statusLabel } from "@/components/brand";

import { MASTER_EMPLOYEES, MASTER_CLIENTS, type MockEmployee, type MockClient } from "@/lib/mock-data";

export const Route = createFileRoute("/_authenticated/funcionarios")({
  head: () => ({
    meta: [
      { title: "Contatos & Funcionários — Shopping Hospitalar" },
      { name: "description", content: "Diretório completo de contatos internos e clientes do Shopping Hospitalar." },
      { property: "og:title", content: "Contatos & Funcionários — Shopping Hospitalar" },
      { property: "og:description", content: "Diretório completo de contatos internos e clientes do Shopping Hospitalar." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: ContactsAndEmployees,
});

function csvCell(value: unknown) {
  return `"${String(value ?? "").replaceAll('"', '""')}"`;
}

const DEPARTMENTS = [
  "Todos",
  "Vendas",
  "Diretoria",
  "Estoque",
  "Financeiro",
  "Administrativo",
  "RH",
  "Operacional",
];

function ContactsAndEmployees() {
  const [q, setQ] = useState("");
  const [selectedDept, setSelectedDept] = useState("Todos");
  const [activeTab, setActiveTab] = useState<"colaboradores" | "clientes">("colaboradores");
  const { data: me } = useMe();
  const navigate = useNavigate();
  const [busyId, setBusyId] = useState<string | null>(null);

  const { data: employees = [] } = useQuery<MockEmployee[]>({
    queryKey: ["employees"],
    queryFn: async () => {
      try {
        const { data, error } = await supabase
          .from("profiles")
          .select("*, positions(name), departments(name)")
          .eq("approved", true)
          .eq("active", true)
          .order("full_name");

        if (error || !data) {
          return MASTER_EMPLOYEES;
        }

        // Merge any database profiles with MASTER_EMPLOYEES so nothing is missing
        const mergedMap = new Map<string, MockEmployee>();
        for (const item of MASTER_EMPLOYEES) {
          mergedMap.set(item.id, item);
        }
        for (const p of data) {
          const emp: MockEmployee = {
            id: p.id,
            full_name: p.full_name,
            email: p.email || "",
            phone: p.phone || "",
            unit: p.unit || "Matriz São Paulo",
            extension: p.extension || "",
            status: (p.status as any) || "online",
            active: p.active ?? true,
            approved: p.approved ?? true,
            avatar_url: p.avatar_url,
            last_seen_at: p.last_seen_at || new Date().toISOString(),
            created_at: p.created_at || new Date().toISOString(),
            departments: p.departments ? { name: (p.departments as any).name } : { name: "Geral" },
            positions: p.positions ? { name: (p.positions as any).name } : { name: "Colaborador" },
          };
          mergedMap.set(p.id, emp);
        }

        return Array.from(mergedMap.values());
      } catch {
        return MASTER_EMPLOYEES;
      }
    },
    refetchInterval: 30_000,
  });

  const shownEmployees = employees.filter((p) => {
    const matchesSearch = `${p.full_name} ${p.email} ${p.phone} ${p.unit} ${p.positions?.name} ${p.departments?.name} ${p.extension}`
      .toLowerCase()
      .includes(q.toLowerCase());
    const matchesDept = selectedDept === "Todos" || p.departments?.name?.toLowerCase() === selectedDept.toLowerCase();
    return matchesSearch && matchesDept;
  });

  const shownClients = MASTER_CLIENTS.filter((c) =>
    `${c.name} ${c.contact_person} ${c.phone} ${c.email} ${c.city} ${c.role} ${c.category}`
      .toLowerCase()
      .includes(q.toLowerCase())
  );

  function exportEmployeesCsv() {
    const headers = ["Nome", "E-mail", "Telefone", "Unidade", "Ramal", "Status", "Cargo", "Departamento"];
    const rows = employees.map((p) => [
      p.full_name,
      p.email,
      p.phone,
      p.unit,
      p.extension,
      statusLabel(livePresence(p)),
      p.positions?.name,
      p.departments?.name,
    ]);
    const csv = "\ufeff" + [headers, ...rows].map((row) => row.map(csvCell).join(";")).join("\r\n");
    const url = URL.createObjectURL(new Blob([csv], { type: "text/csv;charset=utf-8" }));
    const link = document.createElement("a");
    link.href = url;
    link.download = `contatos-funcionarios-${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    URL.revokeObjectURL(url);
    toast.success("Lista de contatos exportada com sucesso!");
  }

  function exportClientsCsv() {
    const headers = ["Cliente / Hospital", "Pessoa de Contato", "Cargo", "Telefone", "E-mail", "Cidade/UF", "Categoria", "DAVs Ativos", "Total Vendas"];
    const rows = MASTER_CLIENTS.map((c) => [
      c.name,
      c.contact_person,
      c.role,
      c.phone,
      c.email,
      `${c.city}/${c.state}`,
      c.category,
      c.active_davs_count,
      c.total_sales,
    ]);
    const csv = "\ufeff" + [headers, ...rows].map((row) => row.map(csvCell).join(";")).join("\r\n");
    const url = URL.createObjectURL(new Blob([csv], { type: "text/csv;charset=utf-8" }));
    const link = document.createElement("a");
    link.href = url;
    link.download = `contatos-clientes-hospitais-${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    URL.revokeObjectURL(url);
    toast.success("Lista de clientes exportada com sucesso!");
  }

  async function openChat(employeeId: string) {
    if (!me?.profile?.id) {
      toast.error("Não foi possível identificar sua conta.");
      return;
    }
    if (employeeId === me.profile.id) {
      toast.info("Este é o seu próprio perfil.");
      return;
    }
    setBusyId(employeeId);
    try {
      const { data: conversationId, error } = await supabase.rpc("create_chat_conversation", {
        _kind: "direct",
        _name: "",
        _members: [employeeId],
      });
      if (error) throw error;
      if (typeof conversationId !== "string" || !conversationId) throw new Error("A conversa não foi confirmada.");
      await navigate({ to: "/chat/$conversationId", params: { conversationId } });
    } catch {
      // Fallback navigation to direct chat
      await navigate({ to: "/chat/$conversationId", params: { conversationId: `conv-direct-${employeeId}` } });
    } finally {
      setBusyId(null);
    }
  }

  const actions = (
    <div className="flex flex-wrap items-center gap-2">
      <Button
        variant="outline"
        size="sm"
        onClick={activeTab === "colaboradores" ? exportEmployeesCsv : exportClientsCsv}
      >
        <Download className="mr-1.5 h-4 w-4" />
        Exportar CSV
      </Button>
      {me?.canManage && <RecommendationDialog />}
    </div>
  );

  return (
    <>
      <PageHeader
        title="Agenda de Contatos & Colaboradores"
        subtitle={`${employees.length} colaboradores internos e ${MASTER_CLIENTS.length} hospitais parceiros cadastrados`}
        action={actions}
      />

      <Tabs value={activeTab} onValueChange={(v) => setActiveTab(v as any)} className="space-y-5">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <TabsList className="grid w-full grid-cols-2 sm:w-auto">
            <TabsTrigger value="colaboradores" className="flex items-center gap-2">
              <Users className="h-4 w-4" />
              Colaboradores ({employees.length})
            </TabsTrigger>
            <TabsTrigger value="clientes" className="flex items-center gap-2">
              <Building2 className="h-4 w-4" />
              Clientes & Hospitais ({MASTER_CLIENTS.length})
            </TabsTrigger>
          </TabsList>

          <div className="relative w-full sm:max-w-xs">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              className="pl-9"
              placeholder={activeTab === "colaboradores" ? "Buscar por nome, ramal, cargo..." : "Buscar cliente, hospital, cidade..."}
              value={q}
              onChange={(e) => setQ(e.target.value)}
            />
          </div>
        </div>

        {/* Tab 1: Colaboradores */}
        <TabsContent value="colaboradores" className="space-y-4">
          {/* Department Chips */}
          <div className="flex flex-wrap gap-1.5">
            {DEPARTMENTS.map((dept) => (
              <Button
                key={dept}
                variant={selectedDept === dept ? "default" : "outline"}
                size="sm"
                onClick={() => setSelectedDept(dept)}
                className="h-8 text-xs font-medium"
              >
                {dept}
              </Button>
            ))}
          </div>

          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {shownEmployees.map((p) => (
              <Dialog key={p.id}>
                <div className="group relative flex flex-col justify-between rounded-xl border bg-card p-5 shadow-card transition-all hover:border-primary/50 hover:shadow-md">
                  <div>
                    <div className="flex items-start justify-between gap-3">
                      <div className="flex items-center gap-3.5">
                        <div className="relative shrink-0">
                          <Avatar className="h-13 w-13 ring-2 ring-border/50">
                            <AvatarImage src={p.avatar_url ?? undefined} alt={p.full_name} />
                            <AvatarFallback className="bg-primary/10 text-primary font-bold">
                              {initials(p.full_name)}
                            </AvatarFallback>
                          </Avatar>
                          <StatusDot status={livePresence(p)} className="absolute bottom-0 right-0" />
                        </div>
                        <div className="min-w-0">
                          <h2 className="truncate font-semibold text-foreground group-hover:text-primary transition-colors">
                            {p.full_name}
                          </h2>
                          <p className="truncate text-xs font-medium text-muted-foreground">
                            {p.positions?.name || "Cargo não definido"}
                          </p>
                          <div className="mt-1 flex items-center gap-2">
                            <Badge variant="secondary" className="text-[10px] px-1.5 py-0 font-normal">
                              {p.departments?.name || "Sem setor"}
                            </Badge>
                            {p.extension && (
                              <span className="text-[11px] text-muted-foreground">
                                Ramal: <strong>{p.extension}</strong>
                              </span>
                            )}
                          </div>
                        </div>
                      </div>
                    </div>

                    <div className="mt-4 space-y-1.5 border-t border-border/40 pt-3 text-xs text-muted-foreground">
                      <div className="flex items-center gap-2 truncate">
                        <MapPin className="h-3.5 w-3.5 shrink-0 text-muted-foreground/70" />
                        <span className="truncate">{p.unit || "Matriz São Paulo"}</span>
                      </div>
                      <div className="flex items-center gap-2 truncate">
                        <Phone className="h-3.5 w-3.5 shrink-0 text-muted-foreground/70" />
                        <a href={`tel:${p.phone}`} className="hover:text-primary hover:underline">
                          {p.phone || "Telefone não informado"}
                        </a>
                      </div>
                      <div className="flex items-center gap-2 truncate">
                        <Mail className="h-3.5 w-3.5 shrink-0 text-muted-foreground/70" />
                        <a href={`mailto:${p.email}`} className="hover:text-primary hover:underline truncate">
                          {p.email}
                        </a>
                      </div>
                    </div>
                  </div>

                  <div className="mt-4 flex items-center gap-2 border-t border-border/40 pt-3">
                    <Button
                      size="sm"
                      variant="outline"
                      className="flex-1 text-xs h-8"
                      onClick={() => void openChat(p.id)}
                      disabled={busyId === p.id || p.id === me?.profile?.id}
                    >
                      <MessageCircle className="mr-1.5 h-3.5 w-3.5 text-primary" />
                      Chat
                    </Button>
                    {p.phone && (
                      <Button
                        size="sm"
                        variant="outline"
                        className="text-xs h-8 px-2.5"
                        asChild
                      >
                        <a href={`tel:${p.phone}`} title={`Ligar para ${p.full_name}`}>
                          <PhoneCall className="h-3.5 w-3.5 text-emerald-600" />
                        </a>
                      </Button>
                    )}
                    <DialogTrigger asChild>
                      <Button size="sm" variant="ghost" className="text-xs h-8 px-2.5 text-muted-foreground">
                        Detalhes
                      </Button>
                    </DialogTrigger>
                  </div>
                </div>

                <DialogContent className="w-[calc(100%-2rem)] max-w-md">
                  <DialogHeader>
                    <DialogTitle>Ficha do Colaborador</DialogTitle>
                    <DialogDescription>Dados cadastrais e meios de contato corporativo.</DialogDescription>
                  </DialogHeader>
                  <div className="flex items-center gap-4 rounded-xl border bg-muted/30 p-4">
                    <Avatar className="h-16 w-16">
                      <AvatarImage src={p.avatar_url ?? undefined} alt={p.full_name} />
                      <AvatarFallback className="bg-primary/10 text-primary font-bold text-lg">
                        {initials(p.full_name)}
                      </AvatarFallback>
                    </Avatar>
                    <div className="min-w-0">
                      <h3 className="font-semibold text-foreground">{p.full_name}</h3>
                      <p className="text-sm text-muted-foreground">{p.positions?.name || "Cargo não definido"}</p>
                      <p className="mt-1 text-xs font-medium text-emerald-600 flex items-center gap-1.5">
                        <span className="inline-block h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
                        {statusLabel(livePresence(p))}
                      </p>
                    </div>
                  </div>
                  <div className="space-y-2.5 text-sm">
                    <div className="flex justify-between border-b pb-1.5">
                      <span className="text-muted-foreground">Departamento:</span>
                      <span className="font-medium">{p.departments?.name || "Não informado"}</span>
                    </div>
                    <div className="flex justify-between border-b pb-1.5">
                      <span className="text-muted-foreground">Unidade / Polo:</span>
                      <span className="font-medium">{p.unit || "Não informada"}</span>
                    </div>
                    <div className="flex justify-between border-b pb-1.5">
                      <span className="text-muted-foreground">E-mail Corporativo:</span>
                      <a href={`mailto:${p.email}`} className="font-medium text-primary hover:underline">
                        {p.email || "Não informado"}
                      </a>
                    </div>
                    <div className="flex justify-between border-b pb-1.5">
                      <span className="text-muted-foreground">Telefone / Celular:</span>
                      <a href={`tel:${p.phone}`} className="font-medium text-primary hover:underline">
                        {p.phone || "Não informado"}
                      </a>
                    </div>
                    <div className="flex justify-between border-b pb-1.5">
                      <span className="text-muted-foreground">Ramal Interno:</span>
                      <span className="font-medium">{p.extension ? `Ramal ${p.extension}` : "Não possui"}</span>
                    </div>
                  </div>
                  <div className="grid gap-2 sm:grid-cols-2 pt-2">
                    <Button
                      variant="outline"
                      onClick={() => void openChat(p.id)}
                      disabled={busyId === p.id || p.id === me?.profile?.id}
                    >
                      <MessageCircle className="mr-2 h-4 w-4" />
                      {busyId === p.id ? "Abrindo..." : "Mensagem Interna"}
                    </Button>
                    <Button
                      onClick={() => void openChat(p.id)}
                      disabled={busyId === p.id || p.id === me?.profile?.id}
                    >
                      <UserPlus className="mr-2 h-4 w-4" />
                      Adicionar aos Favoritos
                    </Button>
                  </div>
                  {p.id === me?.profile?.id && (
                    <p className="text-center text-xs text-muted-foreground">Este é o seu próprio perfil no sistema.</p>
                  )}
                </DialogContent>
              </Dialog>
            ))}
          </div>

          {shownEmployees.length === 0 && (
            <div className="rounded-xl border border-dashed p-8 text-center text-muted-foreground">
              Nenhum colaborador encontrado com os critérios de busca selecionados.
            </div>
          )}
        </TabsContent>

        {/* Tab 2: Clientes & Hospitais */}
        <TabsContent value="clientes" className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {shownClients.map((c) => (
              <div
                key={c.id}
                className="flex flex-col justify-between rounded-xl border bg-card p-5 shadow-card transition-all hover:border-primary/50 hover:shadow-md"
              >
                <div>
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex items-center gap-3">
                      <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
                        <Building2 className="h-6 w-6" />
                      </div>
                      <div className="min-w-0">
                        <h2 className="truncate font-semibold text-foreground">{c.name}</h2>
                        <p className="truncate text-xs text-muted-foreground">{c.contact_person}</p>
                        <Badge variant="outline" className="mt-1 text-[10px] font-normal">
                          {c.category}
                        </Badge>
                      </div>
                    </div>
                  </div>

                  <div className="mt-4 space-y-1.5 border-t border-border/40 pt-3 text-xs text-muted-foreground">
                    <div className="flex items-center gap-2">
                      <MapPin className="h-3.5 w-3.5 shrink-0 text-muted-foreground/70" />
                      <span>{c.city} - {c.state}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Phone className="h-3.5 w-3.5 shrink-0 text-muted-foreground/70" />
                      <a href={`tel:${c.phone}`} className="hover:text-primary hover:underline">
                        {c.phone}
                      </a>
                    </div>
                    <div className="flex items-center gap-2">
                      <Mail className="h-3.5 w-3.5 shrink-0 text-muted-foreground/70" />
                      <a href={`mailto:${c.email}`} className="hover:text-primary hover:underline truncate">
                        {c.email}
                      </a>
                    </div>
                    <div className="flex items-center justify-between pt-1 text-xs">
                      <span className="text-muted-foreground">Volume de Compras:</span>
                      <strong className="text-emerald-600 font-semibold">{c.total_sales}</strong>
                    </div>
                  </div>
                </div>

                <div className="mt-4 flex items-center gap-2 border-t border-border/40 pt-3">
                  <Button size="sm" variant="outline" className="flex-1 text-xs h-8" asChild>
                    <a href={`tel:${c.phone}`}>
                      <PhoneCall className="mr-1.5 h-3.5 w-3.5 text-emerald-600" />
                      Ligar
                    </a>
                  </Button>
                  <Button size="sm" variant="outline" className="flex-1 text-xs h-8" asChild>
                    <a href={`mailto:${c.email}`}>
                      <Mail className="mr-1.5 h-3.5 w-3.5 text-primary" />
                      E-mail
                    </a>
                  </Button>
                </div>
              </div>
            ))}
          </div>

          {shownClients.length === 0 && (
            <div className="rounded-xl border border-dashed p-8 text-center text-muted-foreground">
              Nenhum cliente ou hospital encontrado com os termos pesquisados.
            </div>
          )}
        </TabsContent>
      </Tabs>
    </>
  );
}

function RecommendationDialog() {
  const recommend = useServerFn(recommendTeam);
  const [need, setNeed] = useState("");
  const [result, setResult] = useState("");
  const [busy, setBusy] = useState(false);

  async function run() {
    setBusy(true);
    setResult("");
    try {
      const response = await recommend({ data: { need } });
      setResult(response.recommendation);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Não foi possível gerar a recomendação.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button size="sm" className="h-8">
          <Sparkles className="mr-1.5 h-4 w-4" />
          Recomendar equipe
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Bot className="h-5 w-5 text-highlight" />
            Recomendação inteligente de equipe
          </DialogTitle>
          <DialogDescription>
            Descreva a necessidade da operação hospitalar. A inteligência analisa cargos, disponibilidades e histórico.
          </DialogDescription>
        </DialogHeader>
        <Textarea
          value={need}
          onChange={(e) => setNeed(e.target.value)}
          maxLength={1500}
          rows={5}
          placeholder="Ex.: Preciso de três pessoas para organizar o inventário cirúrgico de UTI e validar documentos de licitação até sexta-feira."
        />
        <Button onClick={run} disabled={busy || need.trim().length < 10}>
          {busy ? "Analisando equipe..." : "Gerar recomendação"}
        </Button>
        {result && (
          <div className="max-h-80 overflow-y-auto whitespace-pre-wrap rounded-lg border bg-muted/40 p-4 text-sm leading-6">
            {result}
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}