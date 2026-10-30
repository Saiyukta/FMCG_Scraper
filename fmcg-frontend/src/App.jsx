import { useEffect, useMemo, useState } from 'react';

const API_URL = 'http://localhost:8080/api/companies';

function App() {
  const [searchTerm, setSearchTerm] = useState('');
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [products, setProducts] = useState([]);
  const [productsLoading, setProductsLoading] = useState(false);
  const [productsError, setProductsError] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalProducts, setTotalProducts] = useState(0);

  const PAGE_SIZE = 12;

  useEffect(() => {
    async function fetchCompanies() {
      try {
        setLoading(true);
        setError('');

        const response = await fetch(API_URL);

        if (!response.ok) {
          throw new Error(`HTTP error: ${response.status}`);
        }

        const data = await response.json();
        setCompanies(data);
      } catch (err) {
        setError(
            'Unable to load companies. Please check whether the backend is running.'
        );
        console.error('Error fetching companies:', err);
      } finally {
        setLoading(false);
      }
    }

    fetchCompanies();
  }, []);


  async function handleViewProducts(company, page = 0) {
    setSelectedCompany(company);
    setProducts([]);
    setProductsError('');
    setProductsLoading(true);
    setCurrentPage(page);

    try {
      const response = await fetch(
          `http://localhost:8080/api/products/search?page=${page}&size=${PAGE_SIZE}&companyId=${company.id}&sortBy=name&direction=asc`
      );

      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }

      const data = await response.json();

      // Spring Boot Page response
      setProducts(data.content);
      setTotalPages(data.totalPages);
      setTotalProducts(data.totalElements);
    } catch (err) {
      console.error('Error fetching products:', err);
      setProductsError('Unable to load products for this company.');
    } finally {
      setProductsLoading(false);
    }
  }


  const filteredCompanies = useMemo(() => {
    const query = searchTerm.trim().toLowerCase();

    return companies.filter(
        (company) =>
            company.name.toLowerCase().includes(query) ||
            company.ticker.toLowerCase().includes(query)
    );
  }, [searchTerm, companies]);

  const listedCount = companies.filter(
      (company) => company.listed
  ).length;

  return (
      <div className="min-h-screen bg-slate-50 text-slate-800">
        {/* Header */}
        <header className="border-b border-slate-200 bg-white">
          <div className="mx-auto flex max-w-7xl items-center justify-between px-5 py-4 sm:px-8">
            <div className="flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-emerald-700 text-2xl font-bold text-white">
                F
              </div>

              <div>
                <h1 className="text-lg font-bold tracking-tight">
                  FMCG Directory
                </h1>
                <p className="text-xs text-slate-500">
                  Indian consumer goods companies
                </p>
              </div>
            </div>

            <span className="hidden rounded-full bg-emerald-50 px-4 py-2 text-xs font-semibold tracking-wide text-emerald-800 sm:inline-flex">
            INDIAN MARKET
          </span>
          </div>
        </header>

        <main className="mx-auto max-w-7xl px-5 py-10 sm:px-8 sm:py-14">
          {/* Introduction */}
          <section className="mb-10">
            <p className="mb-3 text-xs font-bold tracking-[0.2em] text-emerald-700">
              COMPANY DIRECTORY
            </p>

            <h2 className="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
              Explore India's FMCG companies
            </h2>

            <p className="mt-4 max-w-2xl text-sm leading-6 text-slate-500 sm:text-base">
              Discover listed companies, their brands, products and
              product variants in one place.
            </p>
          </section>

          {/* Company directory */}
          {!selectedCompany ? (
              <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
            <div className="border-b border-slate-100 p-6 sm:p-8">
              <h2 className="text-xl font-bold text-slate-900">
                Companies
              </h2>

              <p className="mt-2 text-sm text-slate-500">
                Browse companies and explore their products.
              </p>

              {/* Search */}
              <div className="relative mt-6 max-w-xl">
                <svg
                    className="absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    aria-hidden="true"
                >
                  <circle cx="11" cy="11" r="7" />
                  <path d="m16 16 4 4" />
                </svg>

                <input
                    type="search"
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    placeholder="Search companies by name..."
                    aria-label="Search companies"
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 py-3 pl-12 pr-4 text-sm outline-none transition placeholder:text-slate-400 focus:border-emerald-600 focus:bg-white focus:ring-4 focus:ring-emerald-50"
                />
              </div>
            </div>

            {/* Results count */}
            <div className="flex items-center justify-between border-b border-slate-100 bg-slate-50/70 px-6 py-4 sm:px-8">
              <p className="text-sm font-medium text-slate-600">
                Company results
              </p>

              <span className="rounded-full bg-white px-3 py-1 text-xs font-semibold text-slate-600 ring-1 ring-slate-200">
              {filteredCompanies.length} found
            </span>
            </div>

            {/* Company list */}
            <div className="divide-y divide-slate-100">
              {loading && (
                  <div className="px-6 py-12 text-center text-sm text-slate-500">
                    Loading companies from the database...
                  </div>
              )}

              {!loading && error && (
                  <div className="px-6 py-12 text-center">
                    <p className="font-semibold text-red-700">
                      Could not load companies
                    </p>
                    <p className="mt-2 text-sm text-slate-500">{error}</p>
                    <button
                        type="button"
                        onClick={() => window.location.reload()}
                        className="mt-4 rounded-lg bg-emerald-700 px-4 py-2 text-sm font-semibold text-white hover:bg-emerald-800"
                    >
                      Retry
                    </button>
                  </div>
              )}

              {!loading && !error && filteredCompanies.map((company) => (
                  <article
                      key={company.id}
                      className="flex flex-col gap-4 px-6 py-5 transition hover:bg-slate-50 sm:flex-row sm:items-center sm:px-8"
                  >
                    <div className="flex min-w-0 flex-1 items-center gap-4">
                      <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-emerald-50 text-lg font-bold text-emerald-800">
                        {company.name.charAt(0)}
                      </div>

                      <div className="min-w-0">
                        <h3 className="font-semibold text-slate-900">{company.name}</h3>
                        {/*<p className="mt-1 text-sm text-slate-500">*/}
                        {/*  Ticker: {company.ticker}*/}
                        {/*</p>*/}
                        <p className="mt-1 text-xs text-slate-400">
                          Exchange: {company.exchange || 'N/A'}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center justify-between gap-4 sm:justify-end">
      <span
          className={`rounded-full px-3 py-1.5 text-xs font-semibold ${
              company.listed
                  ? 'bg-emerald-50 text-emerald-700'
                  : 'bg-slate-100 text-slate-600'
          }`}
      >
        {company.listed ? 'Listed' : 'Unlisted'}
      </span>

                      <button
                          type="button"
                          onClick={() => handleViewProducts(company)}
                          className="rounded-lg bg-emerald-700 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-800"
                      >
                        View products
                      </button>
                    </div>
                  </article>
              ))}

              {!loading && !error && filteredCompanies.length === 0 && (
                  <div className="px-6 py-12 text-center">
                    <p className="font-semibold text-slate-700">No companies found</p>
                    <p className="mt-2 text-sm text-slate-500">
                      Try another company name or ticker.
                    </p>
                  </div>
              )}

            </div>

          </section>

          ) : (
              <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
                <div className="border-b border-slate-100 p-6 sm:p-8">
                  <button
                      type="button"
                      onClick={() => setSelectedCompany(null)}
                      className="mb-5 text-sm font-semibold text-emerald-700 hover:text-emerald-900"
                  >
                    ← Back to companies
                  </button>

                  <p className="text-xs font-bold tracking-[0.2em] text-emerald-700">
                    COMPANY PRODUCTS
                  </p>

                  <h2 className="mt-2 text-2xl font-bold text-slate-900">
                    {selectedCompany.name}
                  </h2>

                  <p className="mt-2 text-sm text-slate-500">
                    Ticker: {selectedCompany.ticker}
                  </p>

                  {!productsLoading && !productsError && (
                      <p className="mt-4 text-sm text-slate-500">
                        {products.length} products found
                      </p>
                  )}
                </div>

                {productsLoading && (
                    <p className="px-6 py-12 text-center text-sm text-slate-500">
                      Loading products...
                    </p>
                )}

                {productsError && (
                    <p className="px-6 py-12 text-center text-sm text-red-700">
                      {productsError}
                    </p>
                )}

                {!productsLoading && !productsError && products.length === 0 && (
                    <p className="px-6 py-12 text-center text-sm text-slate-500">
                      No products have been added for this company yet.
                    </p>
                )}


                {!productsLoading && !productsError && products.length > 0 && (
                    <div className="grid grid-cols-1 gap-5 p-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                      {products.map((product) => (
                          <article
                              key={product.id}
                              className="flex min-w-0 flex-col justify-between rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-1 hover:border-emerald-300 hover:shadow-md"
                          >
                            <div>
                              <div className="mb-4 flex h-11 w-11 items-center justify-center rounded-lg bg-emerald-50 text-lg font-bold text-emerald-700">
                                {product.name?.charAt(0).toUpperCase() || 'P'}
                              </div>

                              <h3 className="break-words font-semibold text-slate-900">
                                {product.name}
                              </h3>

                              <p className="mt-2 text-sm text-slate-500">
                                Category: {product.category || 'Uncategorised'}
                              </p>
                            </div>

                            <div className="mt-5 border-t border-slate-100 pt-4">
                              <div className="text-xs text-slate-400">
                                Product ID: {product.id}
                              </div>

                              {product.sourceUrl && (
                                  <a
                                      href={product.sourceUrl}
                                      target="_blank"
                                      rel="noreferrer"
                                      className="mt-3 inline-block text-sm font-medium text-emerald-700 hover:underline"
                                  >
                                    View source ↗
                                  </a>
                              )}
                            </div>
                          </article>
                      ))}
                    </div>
                )}


                {/* Pagination */}
                {!productsLoading && !productsError && totalProducts > 0 && (
                    <div className="flex flex-col items-center justify-between gap-4 border-t border-slate-100 px-6 py-5 sm:flex-row">
                      <p className="text-sm text-slate-500">
                        Page {currentPage + 1} of {totalPages}
                        {' · '}
                        {totalProducts} products
                      </p>

                      <div className="flex items-center gap-2">
                        <button
                            type="button"
                            disabled={currentPage === 0}
                            onClick={() =>
                                handleViewProducts(selectedCompany, currentPage - 1)
                            }
                            className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                          Previous
                        </button>

                        <button
                            type="button"
                            disabled={currentPage >= totalPages - 1}
                            onClick={() =>
                                handleViewProducts(selectedCompany, currentPage + 1)
                            }
                            className="rounded-lg bg-emerald-700 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-800 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                          Next
                        </button>
                      </div>
                    </div>
                )}


              </section>
          )}
        </main>
      </div>
  );
}

export default App;
