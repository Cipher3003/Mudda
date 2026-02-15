import LeftSidebar from "./_components/left-sidebar";

export default function FeedLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="min-h-screen bg-slate-100 font-sans selection:bg-blue-100">
      <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-12 gap-6 px-4">
        <LeftSidebar />
        <div className="col-span-1 md:col-span-12 lg:col-span-9">
          {children}
        </div>
      </div>
    </div>
  );
}
