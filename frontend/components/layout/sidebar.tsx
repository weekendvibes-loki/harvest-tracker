const links = ['Dashboard', 'Farms', 'Harvests', 'Workers', 'Sales', 'Reports'];

export function Sidebar() {
  return (
    <aside className="w-64 border-r border-slate-200 bg-white p-4">
      <nav className="space-y-2">
        {links.map((link) => (
          <div key={link} className="rounded-md px-3 py-2 text-sm text-slate-600 hover:bg-slate-100">
            {link}
          </div>
        ))}
      </nav>
    </aside>
  );
}
