import { useEffect, useState } from "react";
import { addFavorite, clearToken, getFavorites, getMe, getToken, login, removeFavorite, requestRecommendation, saveToken, signup } from "./api.js";

const moods = [["ANY","🤷","아무거나"],["QUICK","⚡","빠르게"],["LIGHT","🌿","가볍게"],["STRESS","🔥","스트레스 해소"],["WARM","☁️","따뜻하게"],["SPECIAL","✨","특별하게"]];
const initialPreferences = { mood:"ANY", category:"ALL", price:"ALL", spice:"ALL", party:"SOLO", excludeMenuId:null };

function SelectField({ label, value, onChange, children }) {
  return <label className="select-field"><span>{label}</span><select value={value} onChange={onChange}>{children}</select></label>;
}

function AuthModal({ initialMode, onClose, onAuthenticated }) {
  const [mode, setMode] = useState(initialMode);
  const [form, setForm] = useState({ nickname:"", email:"", password:"" });
  const [message, setMessage] = useState("");
  const [busy, setBusy] = useState(false);
  const set = (key) => (event) => setForm((current) => ({ ...current, [key]:event.target.value }));
  async function submit(event) {
    event.preventDefault(); setBusy(true); setMessage("");
    try {
      const response = mode === "login" ? await login(form) : await signup(form);
      saveToken(response.token); onAuthenticated(response.user);
    } catch (error) { setMessage(error.message); }
    finally { setBusy(false); }
  }
  return <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
    <section className="auth-modal" role="dialog" aria-modal="true" aria-labelledby="auth-title">
      <button className="modal-close" type="button" onClick={onClose} aria-label="닫기">×</button>
      <p className="modal-kicker">LUNCH PICK</p>
      <h2 id="auth-title">{mode === "login" ? "다시 만나 반가워요" : "점심 취향을 모아봐요"}</h2>
      <p>{mode === "login" ? "찜해둔 메뉴를 이어서 볼 수 있어요." : "마음에 든 메뉴를 언제든 꺼내볼 수 있어요."}</p>
      <form onSubmit={submit}>
        {mode === "signup" && <label>닉네임<input value={form.nickname} onChange={set("nickname")} minLength="2" maxLength="20" required placeholder="점심친구" /></label>}
        <label>이메일<input type="email" value={form.email} onChange={set("email")} required placeholder="hello@example.com" autoComplete="email" /></label>
        <label>비밀번호<input type="password" value={form.password} onChange={set("password")} minLength="8" maxLength="72" required placeholder="8자 이상" autoComplete={mode === "login" ? "current-password" : "new-password"} /></label>
        {message && <p className="form-error" role="alert">{message}</p>}
        <button className="auth-submit" disabled={busy}>{busy ? "잠시만요…" : mode === "login" ? "로그인" : "회원가입"}</button>
      </form>
      <button className="mode-switch" type="button" onClick={() => { setMode(mode === "login" ? "signup" : "login"); setMessage(""); }}>{mode === "login" ? "처음이신가요? 회원가입" : "이미 계정이 있나요? 로그인"}</button>
    </section>
  </div>;
}

function FavoritesDrawer({ favorites, onClose, onRemove }) {
  return <div className="drawer-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
    <aside className="favorites-drawer" aria-label="찜한 메뉴">
      <div className="drawer-title"><div><p>MY PICKS</p><h2>찜한 메뉴</h2></div><button onClick={onClose} aria-label="닫기">×</button></div>
      {favorites.length === 0 ? <div className="empty-favorites"><span>♡</span><p>아직 찜한 메뉴가 없어요.<br />오늘의 추천부터 만나보세요.</p></div> :
        <div className="favorite-list">{favorites.map((menu) => <article key={menu.id}><span>{menu.emoji}</span><div><h3>{menu.name}</h3><p>{menu.categoryLabel} · {menu.priceLabel}</p></div><button type="button" onClick={() => onRemove(menu.id)} aria-label={`${menu.name} 찜 해제`}>♥</button></article>)}</div>}
    </aside>
  </div>;
}

export default function App() {
  const [preferences, setPreferences] = useState(initialPreferences);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [user, setUser] = useState(null);
  const [favorites, setFavorites] = useState([]);
  const [authMode, setAuthMode] = useState(null);
  const [drawerOpen, setDrawerOpen] = useState(false);

  useEffect(() => {
    if (!getToken()) return;
    Promise.all([getMe(), getFavorites()]).then(([me, items]) => { setUser(me); setFavorites(items); }).catch(() => clearToken());
  }, []);
  const update = (key, value) => setPreferences((current) => ({ ...current, [key]:value }));
  const isFavorite = result && favorites.some((menu) => menu.id === result.menu.id);

  async function recommend() {
    setLoading(true); setError("");
    try {
      const next = await requestRecommendation({ ...preferences, excludeMenuId:result?.menu.id ?? null });
      setResult(next);
      window.setTimeout(() => document.querySelector("#result")?.scrollIntoView({ behavior:"smooth", block:"center" }), 50);
    } catch (requestError) {
      setError(requestError.name === "AbortError" ? "서버가 깨어나는 중이에요. 잠시 후 다시 눌러주세요." : requestError.message);
    } finally { setLoading(false); }
  }
  async function toggleFavorite() {
    if (!user) { setAuthMode("login"); return; }
    if (isFavorite) {
      await removeFavorite(result.menu.id); setFavorites((items) => items.filter((item) => item.id !== result.menu.id));
    } else {
      const menu = await addFavorite(result.menu.id); setFavorites((items) => [menu, ...items]);
    }
  }
  function authenticated(nextUser) { setUser(nextUser); setAuthMode(null); getFavorites().then(setFavorites); }
  function logout() { clearToken(); setUser(null); setFavorites([]); setDrawerOpen(false); }
  function reset() { setPreferences(initialPreferences); setResult(null); setError(""); }

  return <>
    <div className="grain" aria-hidden="true" />
    <header className="site-header">
      <a className="logo" href="/" aria-label="Lunch Pick 홈"><span>LP</span> LUNCH PICK</a>
      <nav className="user-nav">
        {user ? <><button className="favorites-link" type="button" onClick={() => setDrawerOpen(true)}>♥ 찜 목록 <b>{favorites.length}</b></button><span>{user.nickname}님</span><button className="logout-link" onClick={logout}>로그아웃</button></> : <><button onClick={() => setAuthMode("login")}>로그인</button><button className="signup-link" onClick={() => setAuthMode("signup")}>회원가입</button></>}
      </nav>
    </header>
    <main>
      <section className="hero"><p className="eyebrow">TODAY&apos;S LITTLE DECISION</p><h1>오늘 점심,<br /><em>뭐 먹지?</em></h1><p className="hero-copy">고민은 짧게, 점심은 맛있게.<br />지금 기분에 딱 맞는 메뉴를 골라드려요.</p><div className="doodle" aria-hidden="true">↘</div></section>
      <section className="picker" aria-labelledby="picker-title">
        <div className="card-heading"><div><span>01</span><h2 id="picker-title">오늘의 취향</h2></div><button type="button" onClick={reset}>초기화</button></div>
        <div className="question"><h3>지금 어떤 점심이 당기나요?</h3><div className="chip-row" role="group" aria-label="기분 선택">{moods.map(([value, emoji, label]) => <button key={value} type="button" className={`chip ${preferences.mood === value ? "active" : ""}`} aria-pressed={preferences.mood === value} onClick={() => update("mood", value)}>{emoji} {label}</button>)}</div></div>
        <div className="select-grid">
          <SelectField label="음식 장르" value={preferences.category} onChange={(e) => update("category", e.target.value)}><option value="ALL">상관없어요</option><option value="KOREAN">한식</option><option value="JAPANESE">일식</option><option value="WESTERN">양식</option><option value="CHINESE">중식</option><option value="ASIAN">아시안</option></SelectField>
          <SelectField label="예산" value={preferences.price} onChange={(e) => update("price", e.target.value)}><option value="ALL">상관없어요</option><option value="VALUE">가성비</option><option value="NORMAL">적당히</option><option value="PREMIUM">오늘은 플렉스</option></SelectField>
          <SelectField label="맵기" value={preferences.spice} onChange={(e) => update("spice", e.target.value)}><option value="ALL">상관없어요</option><option value="NONE">안 매운맛</option><option value="MILD">살짝 매콤</option><option value="HOT">화끈하게</option></SelectField>
          <SelectField label="누구와?" value={preferences.party} onChange={(e) => update("party", e.target.value)}><option value="SOLO">혼밥</option><option value="GROUP">함께</option></SelectField>
        </div>
        {error && <p className="error-message" role="alert">{error}</p>}
        <button className="recommend-button" type="button" onClick={recommend} disabled={loading}><span>{loading ? "맛있는 메뉴 찾는 중…" : "메뉴 골라줘!"}</span><b aria-hidden="true">→</b></button>
      </section>
      {result && <section id="result" className="result" aria-live="polite">
        <div className="tape" aria-hidden="true" /><p className="result-label">TODAY&apos;S PICK</p>
        <button className={`favorite-button ${isFavorite ? "saved" : ""}`} type="button" onClick={toggleFavorite} aria-label={isFavorite ? "찜 해제" : "찜하기"}>{isFavorite ? "♥" : "♡"}<span>{isFavorite ? "찜했어요" : "찜하기"}</span></button>
        <div className="food-emoji" aria-hidden="true">{result.menu.emoji}</div><h2>{result.menu.name}</h2><p className="result-description">{result.menu.description}</p><p className="result-reason">{result.reason}</p>
        <div className="result-tags"><span>{result.menu.categoryLabel}</span><span>{result.menu.priceLabel}</span><span>{result.menu.spiceLabel}</span></div>
        <button className="again-button" type="button" onClick={recommend} disabled={loading}>다른 메뉴도 볼래요 <span>↻</span></button>
        <div className="map-preview"><span>⌖</span><div><b>내 주변 맛집 찾기</b><p>지도 연동 후 거리·길찾기·메뉴 가격을 한눈에 보여드릴게요.</p></div><em>준비 중</em></div>
      </section>}
    </main>
    <footer><span>잘 먹는 것도, 좋은 하루의 일부니까.</span><small>© 2026 LUNCH PICK</small></footer>
    {authMode && <AuthModal initialMode={authMode} onClose={() => setAuthMode(null)} onAuthenticated={authenticated} />}
    {drawerOpen && <FavoritesDrawer favorites={favorites} onClose={() => setDrawerOpen(false)} onRemove={async (id) => { await removeFavorite(id); setFavorites((items) => items.filter((item) => item.id !== id)); }} />}
  </>;
}
