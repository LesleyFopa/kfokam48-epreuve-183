export default function SuccessBanner({ message }) {
  if (!message) return null;
  return (
    <div className="banner success" role="status">
      <span aria-hidden="true">✓</span>
      <span>{message}</span>
    </div>
  );
}
