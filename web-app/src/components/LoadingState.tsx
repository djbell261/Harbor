export function LoadingState({ label = 'Loading Harbor resources' }: { label?: string }) {
  return (
    <div className="space-y-3" aria-live="polite" aria-busy="true">
      <p className="sr-only">{label}</p>
      {[0, 1, 2].map((item) => (
        <div key={item} className="border border-harbor-line bg-white p-4 shadow-soft">
          <div className="h-4 w-2/3 animate-pulse bg-slate-200" />
          <div className="mt-3 h-3 w-1/2 animate-pulse bg-slate-200" />
          <div className="mt-4 grid grid-cols-2 gap-3">
            <div className="h-3 animate-pulse bg-slate-200" />
            <div className="h-3 animate-pulse bg-slate-200" />
          </div>
        </div>
      ))}
    </div>
  );
}
