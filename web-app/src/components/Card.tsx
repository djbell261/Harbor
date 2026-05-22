import type { HTMLAttributes, ReactNode } from 'react';

interface CardProps extends HTMLAttributes<HTMLElement> {
  children: ReactNode;
  as?: 'article' | 'div' | 'section';
}

export function Card({ as = 'div', children, className = '', ...props }: CardProps) {
  const Component = as;

  return (
    <Component
      className={`border border-harbor-line bg-white p-4 shadow-soft sm:p-5 ${className}`}
      {...props}
    >
      {children}
    </Component>
  );
}
