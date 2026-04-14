import { Heart, Pencil, Trash2 } from 'lucide-react';
import { Thought, User } from '../types';
import { motion } from 'motion/react';
import React from 'react';

interface ThoughtCardProps {
  thought: Thought;
  size?: 'featured' | 'default' | 'compact';
  currentUser?: User | null;
  onDelete?: (id: string) => void;
  onEdit?: (thought: Thought) => void;
}

export const ThoughtCard: React.FC<ThoughtCardProps> = ({ thought, size = 'default', currentUser, onDelete, onEdit }) => {
  const initials = thought.author.username.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  const formattedDate = new Date(thought.created).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  const canModify = currentUser?.email === thought.author.email || currentUser?.role === 'ADMIN';

  if (size === 'featured') {
    return (
        <motion.article
            whileHover={{ y: -4, transition: { duration: 0.2 } }}
            className="h-full flex flex-col justify-between p-8 rounded-3xl relative overflow-hidden
          bg-white/50 backdrop-blur-2xl border border-white/80
          shadow-[0_2px_12px_rgba(0,0,0,0.05),0_16px_48px_rgba(0,0,0,0.08)]
          hover:shadow-[0_4px_20px_rgba(0,0,0,0.08),0_24px_64px_rgba(0,0,0,0.13)]
          hover:bg-white/65 transition-all duration-300 cursor-pointer"
        >
          <div className="absolute inset-0 bg-gradient-to-b from-white/40 to-transparent pointer-events-none" />

          <div className="relative">
            {thought.category && (
                <span className="inline-block text-[11px] font-semibold uppercase tracking-wider text-zinc-400 bg-black/5 px-3 py-1 rounded-full mb-5">
              {thought.category.name}
            </span>
            )}
            <h2 className="text-[28px] font-black tracking-tight text-zinc-900 leading-tight mb-4">
              {thought.title}
            </h2>
            <p className="text-[15px] leading-relaxed text-zinc-500 line-clamp-4 mb-6">
              {thought.content}
            </p>
            {thought.tags.length > 0 && (
                <div className="flex flex-wrap gap-2 mb-6">
                  {thought.tags.slice(0, 4).map(tag => (
                      <span key={tag.name} className="text-[12px] font-semibold text-blue-400">#{tag.name}</span>
                  ))}
                </div>
            )}
          </div>

          <div className="relative flex items-center justify-between pt-5 border-t border-black/[0.06]">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-zinc-700 to-zinc-900 flex items-center justify-center text-white text-[11px] font-black">
                {initials}
              </div>
              <div>
                <p className="text-[13px] font-bold text-zinc-800">{thought.author.username}</p>
                <p className="text-[11px] text-zinc-400">{formattedDate}</p>
              </div>
            </div>
            <div className="flex items-center gap-1">
              <button className="p-2 rounded-full text-zinc-400 hover:text-rose-400 hover:bg-rose-50/70 transition-all">
                <Heart size={15} />
              </button>
              {canModify && (
                  <>
                    {onEdit && (
                        <button onClick={e => { e.stopPropagation(); onEdit(thought); }} className="p-2 rounded-full hover:bg-black/5 text-zinc-300 hover:text-zinc-600 transition-all">
                          <Pencil size={13} />
                        </button>
                    )}
                    {onDelete && (
                        <button onClick={e => { e.stopPropagation(); onDelete(thought.id); }} className="p-2 rounded-full hover:bg-red-50 text-zinc-300 hover:text-red-400 transition-all">
                          <Trash2 size={13} />
                        </button>
                    )}
                  </>
              )}
            </div>
          </div>
        </motion.article>
    );
  }

  if (size === 'compact') {
    return (
        <motion.article
            whileHover={{ y: -3, transition: { duration: 0.2 } }}
            className="h-full flex flex-col justify-between p-5 rounded-3xl relative overflow-hidden
          bg-white/50 backdrop-blur-2xl border border-white/80
          shadow-[0_2px_12px_rgba(0,0,0,0.05),0_8px_32px_rgba(0,0,0,0.07)]
          hover:shadow-[0_4px_16px_rgba(0,0,0,0.08),0_16px_48px_rgba(0,0,0,0.1)]
          hover:bg-white/65 transition-all duration-300 cursor-pointer"
        >
          <div className="absolute inset-0 bg-gradient-to-b from-white/40 to-transparent pointer-events-none" />
          <div className="relative">
            {thought.category && (
                <span className="text-[10px] font-semibold uppercase tracking-wider text-zinc-400 bg-black/5 px-2.5 py-0.5 rounded-full mb-3 inline-block">
              {thought.category.name}
            </span>
            )}
            <h2 className="text-[15px] font-bold text-zinc-900 leading-snug tracking-tight mb-2 line-clamp-2">
              {thought.title}
            </h2>
            <p className="text-[13px] leading-relaxed text-zinc-500 line-clamp-2">
              {thought.content}
            </p>
          </div>
          <div className="relative flex items-center justify-between mt-4 pt-3 border-t border-black/[0.05]">
            <div className="flex items-center gap-2">
              <div className="w-6 h-6 rounded-lg bg-gradient-to-br from-zinc-700 to-zinc-900 flex items-center justify-center text-white text-[9px] font-black">
                {initials}
              </div>
              <span className="text-[11px] font-semibold text-zinc-500">{thought.author.username}</span>
              <span className="text-zinc-300">·</span>
              <span className="text-[11px] text-zinc-400">{formattedDate}</span>
            </div>
            {canModify && (
                <div className="flex items-center gap-0.5">
                  {onEdit && (
                      <button onClick={e => { e.stopPropagation(); onEdit(thought); }} className="p-1.5 rounded-full hover:bg-black/5 text-zinc-300 hover:text-zinc-600 transition-all">
                        <Pencil size={11} />
                      </button>
                  )}
                  {onDelete && (
                      <button onClick={e => { e.stopPropagation(); onDelete(thought.id); }} className="p-1.5 rounded-full hover:bg-red-50 text-zinc-300 hover:text-red-400 transition-all">
                        <Trash2 size={11} />
                      </button>
                  )}
                </div>
            )}
          </div>
        </motion.article>
    );
  }

  // default
  return (
      <motion.article
          whileHover={{ y: -4, transition: { duration: 0.2 } }}
          className="h-full flex flex-col justify-between p-6 rounded-3xl relative overflow-hidden
        bg-white/50 backdrop-blur-2xl border border-white/80
        shadow-[0_2px_12px_rgba(0,0,0,0.05),0_12px_40px_rgba(0,0,0,0.08)]
        hover:shadow-[0_4px_16px_rgba(0,0,0,0.08),0_20px_56px_rgba(0,0,0,0.11)]
        hover:bg-white/65 transition-all duration-300 cursor-pointer"
      >
        <div className="absolute inset-0 bg-gradient-to-b from-white/40 to-transparent pointer-events-none" />
        <div className="relative">
          {thought.category && (
              <span className="text-[11px] font-semibold uppercase tracking-wider text-zinc-400 bg-black/5 px-2.5 py-1 rounded-full mb-4 inline-block">
            {thought.category.name}
          </span>
          )}
          <h2 className="text-[18px] font-bold text-zinc-900 leading-snug tracking-tight mb-3">
            {thought.title}
          </h2>
          <p className="text-[13px] leading-relaxed text-zinc-500 line-clamp-3 mb-4">
            {thought.content}
          </p>
          {thought.tags.length > 0 && (
              <div className="flex flex-wrap gap-1.5 mb-4">
                {thought.tags.slice(0, 3).map(tag => (
                    <span key={tag.name} className="text-[11px] font-semibold text-blue-400">#{tag.name}</span>
                ))}
              </div>
          )}
        </div>
        <div className="relative flex items-center justify-between pt-4 border-t border-black/[0.05]">
          <div className="flex items-center gap-2.5">
            <div className="w-7 h-7 rounded-xl bg-gradient-to-br from-zinc-700 to-zinc-900 flex items-center justify-center text-white text-[10px] font-black">
              {initials}
            </div>
            <div>
              <p className="text-[12px] font-bold text-zinc-700">{thought.author.username}</p>
              <p className="text-[10px] text-zinc-400">{formattedDate}</p>
            </div>
          </div>
          <div className="flex items-center gap-1">
            <button className="p-1.5 rounded-full text-zinc-400 hover:text-rose-400 hover:bg-rose-50/70 transition-all">
              <Heart size={13} />
            </button>
            {canModify && (
                <>
                  {onEdit && (
                      <button onClick={e => { e.stopPropagation(); onEdit(thought); }} className="p-1.5 rounded-full hover:bg-black/5 text-zinc-300 hover:text-zinc-600 transition-all">
                        <Pencil size={12} />
                      </button>
                  )}
                  {onDelete && (
                      <button onClick={e => { e.stopPropagation(); onDelete(thought.id); }} className="p-1.5 rounded-full hover:bg-red-50 text-zinc-300 hover:text-red-400 transition-all">
                        <Trash2 size={12} />
                      </button>
                  )}
                </>
            )}
          </div>
        </div>
      </motion.article>
  );
};