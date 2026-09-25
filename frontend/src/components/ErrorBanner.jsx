export default function ErrorBanner({ error }) {
  if (!error) return null;
  return (
    <div className="banner error" role="alert">
      <span aria-hidden="true">⚠</span>
      <span>{error.message ?? "Une erreur est survenue."}</span>
    </div>
  );
}
