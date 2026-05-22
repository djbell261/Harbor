import { Button } from '../../components/Button';
import { Section } from '../../components/Section';
import type { Category } from '../../types/category';

interface CategoryGridProps {
  categories: Category[];
  selectedCategory: string;
  onSelectCategory: (category: string) => void;
}

export function CategoryGrid({
  categories,
  selectedCategory,
  onSelectCategory
}: CategoryGridProps) {
  return (
    <Section
      title="Categories"
      description="Choose a resource type."
      actions={
        selectedCategory ? (
          <Button variant="quiet" onClick={() => onSelectCategory('')}>
            Clear
          </Button>
        ) : null
      }
    >
      <div className="grid grid-cols-1 gap-2 min-[380px]:grid-cols-2 sm:grid-cols-3 lg:grid-cols-4">
        {categories.map((category) => {
          const selected = category.code === selectedCategory;

          return (
            <button
              key={category.id}
              type="button"
              className={`min-h-24 border p-3 text-left shadow-soft transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-harbor-blue sm:p-4 ${
                selected
                  ? 'border-harbor-blue bg-blue-50'
                  : 'border-harbor-line bg-white hover:border-harbor-blue'
              }`}
              onClick={() => onSelectCategory(selected ? '' : category.code)}
            >
              <span className="block text-sm font-semibold text-harbor-ink">{category.name}</span>
              {category.description && (
                <span className="mt-1 line-clamp-2 block text-xs leading-5 text-harbor-muted">
                  {category.description}
                </span>
              )}
            </button>
          );
        })}
      </div>
    </Section>
  );
}
