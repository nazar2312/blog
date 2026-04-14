import React, { useState } from 'react';
import { View, User } from '../types';
import { motion, AnimatePresence } from 'motion/react';
import { Home, PenLine, LogOut, Search, Loader2, Check } from 'lucide-react';

interface TopBarProps {
  showPublish: boolean;
  onPublish: () => void;
  publishing: 'idle' | 'loading' | 'success';
  currentView: View;
  onViewChange: (view: View) => void;
  user: User | null;
  onLogout: () => void;
  onLogin: (credentials: { email: string; password: string }) => Promise<void>;
  onRegister: (data: { username: string; email: string; password: string }) => Promise<void>;
  isEditing?: boolean;
}

export const TopBar: React.FC<TopBarProps> = ({
                                                showPublish,
                                                onPublish,
                                                publishing,
                                                currentView,
                                                onViewChange,
                                                user,
                                                onLogout,
                                                onLogin,
                                                onRegister,
                                                isEditing = false,
                                              }) => {
  const [authModal, setAuthModal] = useState<'none' | 'login' | 'register'>('none');
  const [authStep, setAuthStep] = useState<'form' | 'success'>('form');
  const [authLoading, setAuthLoading] = useState(false);
  const [authError, setAuthError] = useState('');
  const [loginForm, setLoginForm] = useState({ email: '', password: '' });
  const [registerForm, setRegisterForm] = useState({ username: '', email: '', password: '' });
  const [searchQuery, setSearchQuery] = useState('');

  const publishLabel = {
    idle: isEditing ? 'Update' : 'Publish',
    loading: isEditing ? 'Updating...' : 'Publishing...',
    success: isEditing ? 'Updated!' : 'Published!',
  }[publishing];

  const openAuth = (mode: 'login' | 'register') => {
    setAuthModal(mode);
    setAuthStep('form');
    setAuthError('');
    setLoginForm({ email: '', password: '' });
    setRegisterForm({ username: '', email: '', password: '' });
  };

  const closeAuth = () => {
    setAuthModal('none');
    setAuthStep('form');
    setAuthError('');
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setAuthLoading(true);
    setAuthError('');
    try {
      await onLogin(loginForm);
      setAuthStep('success');
      setTimeout(() => closeAuth(), 1800);
    } catch (err: any) {
      setAuthError(err.message || 'Invalid credentials.');
    } finally {
      setAuthLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setAuthLoading(true);
    setAuthError('');
    try {
      await onRegister(registerForm);
      setAuthStep('success');
      setTimeout(() => closeAuth(), 1800);
    } catch (err: any) {
      setAuthError(err.message || 'Registration failed.');
    } finally {
      setAuthLoading(false);
    }
  };

  const displayName = authModal === 'login' ? loginForm.email.split('@')[0] : registerForm.username;

  return (
      <>
        <header className="relative fixed top-0 inset-x-0 z-50 h-14 bg-white/60 backdrop-blur-xl border-b border-white/20 shadow-[0_2px_20px_rgba(0,0,0,0.06)] flex items-center px-4">
          {/* Logo + Left Nav */}
          <div className="flex items-center gap-3 mr-6">
            <div className="font-black text-[15px] tracking-tighter select-none">thoughts</div>
            <nav className="hidden sm:flex items-center gap-2">
              {user && (
                <NavBtn
                  icon={PenLine}
                  label="Write"
                  active={currentView === 'compose'}
                  onClick={() => onViewChange('compose')}
                />
              )}
            </nav>
          </div>

          {/* Centered Home + Search */}
          <div className="absolute left-1/2 -translate-x-1/2 w-full max-w-md px-4 hidden sm:block">
            <div className="flex items-center gap-3 bg-white/70 backdrop-blur-xl border border-white/60 px-4 py-2 rounded-full shadow-[inset_0_1px_0_rgba(255,255,255,0.6),0_6px_20px_rgba(0,0,0,0.06)]">
              <NavBtn
                icon={Home}
                label="Home"
                active={currentView === 'discovery'}
                onClick={() => onViewChange('discovery')}
              />
              <Search size={16} className="text-black/30 flex-shrink-0" />
              <input
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search thoughts..."
                className="flex-1 text-sm outline-none placeholder-black/30 bg-transparent"
              />
            </div>
          </div>

          {/* Right side (Auth / User controls) */}
          <div className="ml-auto flex items-center gap-2">

            {user ? (
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-black/40 hidden sm:block">{user.username}</span>
                  <button
                      onClick={onLogout}
                      className="p-2 rounded-full text-black/40 hover:text-black hover:bg-black/5 transition-all"
                      title="Log out"
                  >
                    <LogOut size={16} />
                  </button>
                </div>
            ) : (
                <div className="flex items-center gap-2">
                  <button
                      onClick={() => openAuth('login')}
                      className="text-xs font-bold text-black/50 hover:text-black transition-colors px-3 py-1.5"
                  >
                    Sign in
                  </button>
                  <button
                      onClick={() => openAuth('register')}
                      className="text-xs font-bold bg-black text-white px-3 py-1.5 hover:bg-zinc-800 transition-colors"
                  >
                    Join
                  </button>
                </div>
            )}

            {/* Publish / Update button */}
            <AnimatePresence>
              {showPublish && (
                  <motion.button
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.9 }}
                      transition={{ duration: 0.15 }}
                      onClick={onPublish}
                      disabled={publishing !== 'idle'}
                      className={`ml-1 flex items-center gap-1.5 px-4 py-1.5 text-xs font-bold uppercase tracking-widest transition-all disabled:opacity-70 ${
                          publishing === 'success'
                              ? 'bg-emerald-500 text-white'
                              : 'bg-black text-white hover:bg-zinc-800'
                      }`}
                  >
                    {publishing === 'loading' && <Loader2 size={12} className="animate-spin" />}
                    {publishing === 'success' && <Check size={12} />}
                    {publishLabel}
                  </motion.button>
              )}
            </AnimatePresence>
          </div>
        </header>


        {/* Auth modal */}
        <AnimatePresence>
          {authModal !== 'none' && (
              <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  transition={{ duration: 0.2 }}
                  onClick={closeAuth}
                  className="fixed inset-0 z-[100] flex items-center justify-center px-6 bg-black/20 backdrop-blur-xl"
              >
                <motion.div
                    initial={{ scale: 0.9, opacity: 0, y: 20 }}
                    animate={{ scale: 1, opacity: 1, y: 0 }}
                    exit={{ scale: 0.9, opacity: 0, y: 20 }}
                    transition={{ type: 'spring', damping: 25, stiffness: 300 }}
                    onClick={(e) => e.stopPropagation()}
                    className="w-full max-w-sm bg-white border border-black/10 shadow-[0_32px_64px_-12px_rgba(0,0,0,0.15)] overflow-hidden"
                >
                  <AnimatePresence mode="wait">
                    {authStep === 'success' ? (
                        <motion.div
                            key="success"
                            initial={{ opacity: 0, scale: 0.95 }}
                            animate={{ opacity: 1, scale: 1 }}
                            exit={{ opacity: 0, scale: 0.95 }}
                            transition={{ type: 'spring', damping: 20, stiffness: 300 }}
                            className="p-10 flex flex-col items-center text-center"
                        >
                          <motion.div
                              initial={{ scale: 0 }}
                              animate={{ scale: 1 }}
                              transition={{ type: 'spring', damping: 15, stiffness: 400, delay: 0.05 }}
                              className="w-16 h-16 rounded-full bg-black flex items-center justify-center text-white text-2xl font-black mb-5"
                          >
                            {displayName?.[0]?.toUpperCase() || '?'}
                          </motion.div>
                          <motion.div
                              initial={{ scale: 0 }}
                              animate={{ scale: 1 }}
                              transition={{ type: 'spring', damping: 15, stiffness: 400, delay: 0.15 }}
                              className="w-6 h-6 rounded-full bg-emerald-500 flex items-center justify-center -mt-7 mb-5 ml-8"
                          >
                            <Check size={14} className="text-white" />
                          </motion.div>
                          <motion.p
                              initial={{ opacity: 0, y: 8 }}
                              animate={{ opacity: 1, y: 0 }}
                              transition={{ delay: 0.2 }}
                              className="text-xl font-black tracking-tight"
                          >
                            Welcome{authModal === 'login' ? ' back' : ''}{displayName ? `, ${displayName}` : ''}!
                          </motion.p>
                          <motion.p
                              initial={{ opacity: 0 }}
                              animate={{ opacity: 1 }}
                              transition={{ delay: 0.3 }}
                              className="text-sm text-black/40 mt-1"
                          >
                            {authModal === 'login' ? 'Signing you in...' : 'Account created.'}
                          </motion.p>
                        </motion.div>
                    ) : (
                        <motion.div
                            key="form"
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            className="p-8"
                        >
                          <h2 className="text-xl font-black tracking-tighter mb-1">
                            {authModal === 'login' ? 'Sign in.' : 'Create account.'}
                          </h2>
                          <p className="text-sm text-black/40 mb-6">
                            {authModal === 'login' ? "Good to see you again." : "Join the conversation."}
                          </p>

                          {authError && (
                              <div className="mb-4 px-4 py-3 bg-red-50 border border-red-100 text-xs text-red-600 font-medium">
                                {authError}
                              </div>
                          )}

                          {authModal === 'login' ? (
                              <form onSubmit={handleLogin} className="space-y-3">
                                <input
                                    type="email"
                                    placeholder="Email"
                                    value={loginForm.email}
                                    onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })}
                                    className="w-full border border-black/15 px-4 py-3 text-sm outline-none focus:border-black transition-colors"
                                    required
                                />
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={loginForm.password}
                                    onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
                                    className="w-full border border-black/15 px-4 py-3 text-sm outline-none focus:border-black transition-colors"
                                    required
                                />
                                <button
                                    type="submit"
                                    disabled={authLoading}
                                    className="w-full bg-black text-white px-4 py-3 text-xs font-bold uppercase tracking-widest hover:bg-zinc-800 transition-all disabled:opacity-50 flex items-center justify-center gap-2 mt-2"
                                >
                                  {authLoading && <Loader2 size={13} className="animate-spin" />}
                                  Sign in
                                </button>
                              </form>
                          ) : (
                              <form onSubmit={handleRegister} className="space-y-3">
                                <input
                                    type="text"
                                    placeholder="Username"
                                    value={registerForm.username}
                                    onChange={(e) => setRegisterForm({ ...registerForm, username: e.target.value })}
                                    className="w-full border border-black/15 px-4 py-3 text-sm outline-none focus:border-black transition-colors"
                                    required
                                />
                                <input
                                    type="email"
                                    placeholder="Email"
                                    value={registerForm.email}
                                    onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })}
                                    className="w-full border border-black/15 px-4 py-3 text-sm outline-none focus:border-black transition-colors"
                                    required
                                />
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={registerForm.password}
                                    onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })}
                                    className="w-full border border-black/15 px-4 py-3 text-sm outline-none focus:border-black transition-colors"
                                    required
                                />
                                <button
                                    type="submit"
                                    disabled={authLoading}
                                    className="w-full bg-black text-white px-4 py-3 text-xs font-bold uppercase tracking-widest hover:bg-zinc-800 transition-all disabled:opacity-50 flex items-center justify-center gap-2 mt-2"
                                >
                                  {authLoading && <Loader2 size={13} className="animate-spin" />}
                                  Create account
                                </button>
                              </form>
                          )}

                          <p className="text-xs text-black/40 text-center mt-4">
                            {authModal === 'login' ? "No account? " : "Already have one? "}
                            <button
                                onClick={() => openAuth(authModal === 'login' ? 'register' : 'login')}
                                className="font-bold text-black underline underline-offset-2"
                            >
                              {authModal === 'login' ? 'Join' : 'Sign in'}
                            </button>
                          </p>
                        </motion.div>
                    )}
                  </AnimatePresence>
                </motion.div>
              </motion.div>
          )}
        </AnimatePresence>
      </>
  );
};

const NavBtn = ({ icon: Icon, label, active, onClick }: { icon: any; label: string; active: boolean; onClick: () => void }) => (
    <button
        onClick={onClick}
        className={`flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold transition-all rounded-full border backdrop-blur-md ${
            active
                ? 'text-zinc-900 bg-white/70 border-white/60 shadow-[inset_0_1px_0_rgba(255,255,255,0.6),0_6px_20px_rgba(0,0,0,0.06)]'
                : 'text-black/50 hover:text-zinc-900 bg-white/30 hover:bg-white/50 border-white/40 hover:border-white/60'
        }`}
    >
      <Icon size={14} />
      <span className="hidden sm:block">{label}</span>
    </button>
);