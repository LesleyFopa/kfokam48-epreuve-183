export default function ErrorBanner({ error }) {
  if (!error) return null;
  return (
    <div className="banner error" role="alert">
      {error.message ?? "Une erreur est survenue."}
    </div>
  );
}
